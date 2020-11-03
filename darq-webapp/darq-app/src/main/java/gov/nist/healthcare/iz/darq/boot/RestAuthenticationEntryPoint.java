package gov.nist.healthcare.iz.darq.boot;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import gov.nist.healthcare.auth.exception.PendingVerificationException;
import gov.nist.healthcare.domain.OpAck;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public final void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {

        String message;
        boolean removeCookie = false;
		OpAck.AckStatus status = OpAck.AckStatus.FAILED;

        if (e instanceof BadCredentialsException) {
			message = "Invalid username or password";
			removeCookie = true;
			response.setStatus(403);
		} else if (e instanceof DisabledException) {
        	message = "Account pending approval";
			removeCookie = true;
			status = OpAck.AckStatus.WARNING;
			response.setStatus(403);
		} else if (e instanceof PendingVerificationException) {
			message = "Account pending email verification, please verify your email address or contact administrator.";
			status = OpAck.AckStatus.WARNING;
			removeCookie = true;
			response.setStatus(403);
		} else if (e instanceof LockedException) {
			message = "Locked account";
			removeCookie = true;
			response.setStatus(403);
		} else if (e instanceof CredentialsExpiredException) {
			message = "Token Expired";
			removeCookie = true;
			response.setStatus(403);
		} else if (e instanceof AccountExpiredException) {
			message = "Account Expired";
			removeCookie = true;
			response.setStatus(403);
		} else if (e instanceof AuthenticationServiceException) {
			message = e.getMessage();
			response.setStatus(400);
		} else if (e instanceof InsufficientAuthenticationException) {
			message = "Unauthorized request, no authentication found";
			response.setStatus(401);
		}
        else {
			message = e.getLocalizedMessage();
			response.setStatus(400);
		}
        OpAck<Object> payload = new OpAck<>(status, message, null, "AUTHENTICATION");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        if(removeCookie) {
			Cookie authCookie = new Cookie("authCookie", "");
			authCookie.setPath("/");
			authCookie.setMaxAge(0);
			authCookie.setHttpOnly(true);
			response.addCookie(authCookie);
		}
        mapper.writeValue(response.getWriter(), payload);           
	}

}