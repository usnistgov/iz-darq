package gov.nist.healthcare.iz.darq.users.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import gov.nist.healthcare.auth.exception.PendingVerificationException;
import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.access.domain.Permission;
import gov.nist.healthcare.iz.darq.access.domain.UserRole;
import gov.nist.healthcare.iz.darq.model.FacilityDescriptor;
import gov.nist.healthcare.iz.darq.users.domain.User;
import gov.nist.healthcare.iz.darq.users.domain.UserAccount;
import gov.nist.healthcare.iz.darq.users.facility.service.FacilityService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

public class AuthenticationService implements gov.nist.healthcare.auth.service.AuthenticationService<UserAccount, UserRole, User> {

    private final UserManagementService userManagementService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final FacilityService facilityService;
    private final CryptoKey keys;
    private final String COOKIE_NAME;
    private final long DURATION;

    public AuthenticationService(
            UserManagementService userManagementService,
            AuthenticationEntryPoint authenticationEntryPoint,
            FacilityService facilityService,
            CryptoKey keys,
            String cookie_name,
            long duration) {
        this.userManagementService = userManagementService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.facilityService = facilityService;
        this.keys = keys;
        COOKIE_NAME = cookie_name;
        DURATION = duration;
    }

    @Override
    public AbstractAuthenticationToken validateTokenAndGetPrincipal(Jws<Claims> token) throws AuthenticationException {
        UserAccount account = this.userManagementService.findAccountById(token.getBody().getSubject());
        try {
            this.verifyAccount(account);
        } catch (DisabledException e) {
            if(!token.getBody().get("source").equals("AART")) {
                throw e;
            }
        }

        User user = this.createPrincipal(account, token.getBody().get("facility", ArrayList.class));
        return new UsernamePasswordAuthenticationToken(
                user,
                token,
                user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet())
        );
    }

    public void setUserFacilityPermissions(User user, ArrayList<String> facilities) {
        Set<Permission> facilityPermissions = user.isAdministrator() ?
                new HashSet<>(Arrays.asList(Permission.DATA_FACILITY_VIEW, Permission.DATA_FACILITY_AUTHOR, Permission.DATA_FACILITY_UPLOAD, Permission.DATA_FACILITY_PUBLISH)) :
                new HashSet<>(Arrays.asList(Permission.DATA_FACILITY_VIEW, Permission.DATA_FACILITY_UPLOAD));

        // Add Facility Permissions
        this.facilityService.getUserFacilities(user).stream()
        .map(FacilityDescriptor::getId)
        .forEach((f) -> facilityPermissions.forEach(perm -> user.getPermissions().putFacilityPermission(perm, user, f)));

        // Get Token Facilities
        if(facilities != null) {
            facilities.stream()
                    .map(this.facilityService::findByName)
                    .filter(Objects::nonNull)
                    .map(FacilityDescriptor::getId)
                    .forEach((f) -> facilityPermissions.forEach(perm -> user.getPermissions().putFacilityPermission(perm, user, f)));
        }
    }

    @Override
    public void verifyAccountAndHandleLoginResponse(HttpServletRequest request, HttpServletResponse response, UserAccount account) throws IOException, ServletException, InvalidKeySpecException, NoSuchAlgorithmException {
        try {
            User user = this.login(request, response, account);
            OpAck<User> userOpAck = new OpAck<>(OpAck.AckStatus.SUCCESS, "Login Success", user, "LOGIN");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getWriter(), userOpAck);
        } catch (Exception exception) {
            this.authenticationEntryPoint.commence(request, response, new PendingVerificationException(account.getUsername()));
        }
    }

    @Override
    public Cookie createAuthCookie(UserAccount account, Date expiresAt, ArrayList<String> facilities) throws Exception {
        Claims claims = Jwts.claims();
        claims.put("roles", account
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        int duration = (int) ((expiresAt.getTime() - System.currentTimeMillis()) / 1000);

        String JWT = Jwts.builder()
                .setClaims(this.jwtClaims(account, claims))
                .claim("facility", facilities)
                .setSubject(account.getId())
                .setExpiration(expiresAt)
                .signWith(SignatureAlgorithm.RS256, keys.getPrivateKey()).compact();

        //-- Create Cookie
        Cookie authCookie = new Cookie(COOKIE_NAME, JWT);
        authCookie.setPath("/");
        authCookie.setMaxAge(duration);
        authCookie.setHttpOnly(true);

        return authCookie;
    }

    @Override
    public void clearLoginCookie(HttpServletResponse response) {
        Cookie authCookie = new Cookie(COOKIE_NAME, "");
        authCookie.setPath("/");
        authCookie.setMaxAge(0);
        authCookie.setHttpOnly(true);
        response.addCookie(authCookie);
    }


    @Override
    public Cookie createAuthCookieWithDefaultDuration(UserAccount account, ArrayList<String> facilities) throws Exception {
        return this.createAuthCookie(account, new Date(System.currentTimeMillis() + DURATION * 1000), null);
    }

    @Override
    public User verifyAccountAndCreatePrincipal(UserAccount account, ArrayList<String> facilities) throws AuthenticationException {
        this.verifyAccount(account);
        return this.createPrincipal(account, facilities);
    }

    public void verifyAccount(UserAccount account) throws AuthenticationException {
        if(account == null) {
            throw new BadCredentialsException("Invalid credentials");
        }

        if(!account.isVerified()) {
            throw new PendingVerificationException(account.getId());
        }

        if(account.isLocked()) {
            throw new LockedException(account.getId());
        }

        if(account.isPending()) {
            throw new DisabledException(account.getId());
        }
    }

    public User createPrincipal(UserAccount account, ArrayList<String> facilities) {
        User user = this.userManagementService.fromAccount(account);
        this.setUserFacilityPermissions(user, facilities);
        return user;
    }

    @Override
    public User login(HttpServletRequest request, HttpServletResponse response, UserAccount account) throws Exception {
        User user = this.verifyAccountAndCreatePrincipal(account, null);
        Cookie authCookie = this.createAuthCookieWithDefaultDuration(account, null);
        response.setContentType("application/json");
        response.addCookie(authCookie);
        return user;
    }

    @Override
    public Claims jwtClaims(UserAccount account, Claims claims) {
        claims.put("source", Strings.isNullOrEmpty(account.getSource()) ? "qDAR" : account.getSource());
        return claims;
    }

}
