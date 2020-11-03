package gov.nist.healthcare.auth.config;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import gov.nist.healthcare.auth.domain.Privilege;
import gov.nist.healthcare.auth.service.AuthenticationValidationService;
import gov.nist.healthcare.auth.service.CryptoKey;
import gov.nist.healthcare.domain.OpAck;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.domain.LoginResponse;
import gov.nist.healthcare.auth.domain.User;
import gov.nist.healthcare.auth.service.AccountService;

@Component
public class TokenAuthenticationService {

	@Value( "${token.expiration.seconds}" )
	int DURATION;
	@Autowired
	private CryptoKey keys;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private AccountService accountService;
	@Autowired
	AuthenticationValidationService authenticationValidationService;

	public void addAuthentication(HttpServletResponse res, String username) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		Account account = accountService.getAccountByUsername(username);

		Claims claims = Jwts.claims();
		claims.put("roles", account
							.getPrivileges()
							.stream()
							.map(Privilege::getAuthority)
							.collect(Collectors.toList()));

		String JWT = Jwts.builder()
					.setClaims(this.authenticationValidationService.jwtClaims(account, claims))
					.setSubject(username)
					.setExpiration(new Date(System.currentTimeMillis() + DURATION * 1000))
					.signWith(SignatureAlgorithm.RS256, keys.getPrivateKey()).compact();

		//-- Create Cookie
		Cookie authCookie = new Cookie("authCookie", JWT);
		authCookie.setPath("/");
		authCookie.setMaxAge(DURATION);
		authCookie.setHttpOnly(true);

		
		//-- Create Payload
		OpAck<User> loginResponse = new OpAck<>(OpAck.AckStatus.SUCCESS, "Login Success", new User(account.getId(), account.getUsername(), new ArrayList<>(account.getPrivileges())), "LOGIN");
				
		//-- Create response
		res.setContentType("application/json");
		res.addCookie(authCookie);
		objectMapper.writeValue(res.getWriter(), loginResponse);

	}

	public AbstractAuthenticationToken getAuthentication(HttpServletRequest request) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		Cookie token = WebUtils.getCookie(request,"authCookie");
		if (token != null && token.getValue() != null && !token.getValue().isEmpty()) {
			Jws<Claims> user = Jwts.parser()
					.setSigningKey(keys.getPublicKey())
					.parseClaimsJws(token.getValue());

			return this.authenticationValidationService.validateTokenAndGetPrincipal(user);

			Account userDetails = accountService.getAccountByUsername(user);
			if(userDetails == null) {
				throw new BadCredentialsException("Invalid account");
			}
			if(userDetails.isLocked()) {
				throw new LockedException("Your account has been locked, please contact web administrator for further details");
			}
			return new UsernamePasswordAuthenticationToken(userDetails, token.getValue(), userDetails.getPrivileges());
		}
		return null;
	}
}