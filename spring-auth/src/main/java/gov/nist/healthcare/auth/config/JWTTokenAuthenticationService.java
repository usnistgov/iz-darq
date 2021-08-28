package gov.nist.healthcare.auth.config;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import gov.nist.healthcare.auth.domain.Authority;
import gov.nist.healthcare.auth.service.AuthenticationService;
import gov.nist.healthcare.crypto.service.CryptoKey;
import io.jsonwebtoken.*;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.web.util.WebUtils;
import gov.nist.healthcare.auth.domain.Account;

public class JWTTokenAuthenticationService<T extends Account<E>, E extends Authority, P> {

	private final String COOKIE_NAME;
	private final CryptoKey keys;
	private final AuthenticationService<T, E, P> authenticationService;

	public JWTTokenAuthenticationService(String COOKIE_NAME, CryptoKey keys, AuthenticationService<T, E, P> authenticationService) {
		this.COOKIE_NAME = COOKIE_NAME;
		this.keys = keys;
		this.authenticationService = authenticationService;
	}

	public AbstractAuthenticationToken getAuthentication(HttpServletRequest request) throws Exception {
		Cookie token = WebUtils.getCookie(request,COOKIE_NAME);
		if (token != null && token.getValue() != null && !token.getValue().isEmpty()) {
			Jws<Claims> jwt = Jwts.parser()
					.setSigningKey(keys.getPublicKey())
					.parseClaimsJws(token.getValue());

			return this.authenticationService.validateTokenAndGetPrincipal(jwt);
		} else {
			return null;
		}
	}
}