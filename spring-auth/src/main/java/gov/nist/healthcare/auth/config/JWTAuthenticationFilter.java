package gov.nist.healthcare.auth.config;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.domain.Authority;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.GenericFilterBean;


public class JWTAuthenticationFilter<T extends Account<E>, E extends Authority, P> extends GenericFilterBean {
	
	private final JWTTokenAuthenticationService<T, E, P> tokenService;
	private final AuthenticationEntryPoint handler;

	public JWTAuthenticationFilter(JWTTokenAuthenticationService<T, E, P> tokenService, AuthenticationEntryPoint handler) {
		this.tokenService = tokenService;
		this.handler = handler;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		try {
			AbstractAuthenticationToken authentication = tokenService.getAuthentication((HttpServletRequest) request);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			filterChain.doFilter(request, response);
		}
		catch (AuthenticationException exception) {
			exception.printStackTrace();
			this.handler.commence((HttpServletRequest) request, (HttpServletResponse)  response, exception);
			SecurityContextHolder.clearContext();
		}
		catch (Exception e) {
			e.printStackTrace();
			SecurityContextHolder.clearContext();
		}
	}
}