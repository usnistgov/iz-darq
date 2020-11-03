package gov.nist.healthcare.iz.darq.users.service.impl;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.iz.darq.users.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class AuthenticationTokenService implements gov.nist.healthcare.auth.service.AuthenticationTokenService  {

    @Autowired
    UserManagementService userManagementService;

    @Override
    public AbstractAuthenticationToken validateTokenAndGetPrincipal(Jws<Claims> token) throws AuthenticationException {
        User user = this.userManagementService.findUserByUsername(token.getBody().getSubject());
        if(user == null) {
            throw new BadCredentialsException("Invalid account");
        }

        switch (user.getStatus()) {
            case LOCKED:
                throw new LockedException(token.getBody().getSubject());
            case PENDING:
                throw new DisabledException(token.getBody().getSubject());
            default:
                return new UsernamePasswordAuthenticationToken(
                        user,
                        token,
                        user.getPrivileges().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet())
                );

        }
    }

    @Override
    public Claims jwtClaims(Account account, Claims claims) {
        return claims;
    }

    @Override
    public <T> T getAuthenticationPrincipalAs(Class<T> clazz) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication.getPrincipal() == null) {
			return null;
		}
		if(clazz.isInstance(authentication.getPrincipal())) {
		    return (T) authentication.getPrincipal();
        }

		return null;
    }
}
