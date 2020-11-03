package gov.nist.healthcare.auth.config;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.domain.Authority;
import gov.nist.healthcare.auth.service.AuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nist.healthcare.auth.domain.LoginRequest;

public class JWTLoginFilter<T extends Account<E>, E extends Authority, P> extends AbstractAuthenticationProcessingFilter {

	private final AuthenticationEntryPoint handler;
	private final Class<T> accountClazz;
	private final AuthenticationService<T, E, P> authenticationService;

	public JWTLoginFilter(String url, Class<T> accountClazz, AuthenticationManager authManager, AuthenticationEntryPoint handler, AuthenticationService<T, E, P> authenticationService) {
		super(new AntPathRequestMatcher(url));
		this.handler = handler;
		this.accountClazz = accountClazz;
		this.authenticationService = authenticationService;
		setAuthenticationManager(authManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException, IOException {
		LoginRequest credentials = new ObjectMapper().readValue(req.getInputStream(), LoginRequest.class);
		return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(credentials.getUsername(),credentials.getPassword(), Collections.emptyList()));
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException, ServletException {
		try {
			if(auth instanceof UsernamePasswordAuthenticationToken && accountClazz.isAssignableFrom(auth.getPrincipal().getClass())) {
				T userAccount = (T) auth.getPrincipal();
				this.authenticationService.verifyAccountAndHandleLoginResponse(req, res, userAccount);
			} else {
				handler.commence(req, res, new AuthenticationServiceException(""));
			}
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
		handler.commence(request, response, failed);
	}
	
}