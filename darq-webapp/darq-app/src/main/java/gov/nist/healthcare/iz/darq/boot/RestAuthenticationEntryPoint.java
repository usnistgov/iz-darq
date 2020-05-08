package gov.nist.healthcare.iz.darq.boot;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import gov.nist.healthcare.auth.domain.User;
import gov.nist.healthcare.domain.OpAck;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nist.healthcare.auth.domain.LoginResponse;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public final void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws JsonGenerationException, JsonMappingException, IOException {

        String message = "";
        if (e instanceof BadCredentialsException) {
			message = "Invalid username or password";
		} 
        else if (e instanceof DisabledException) {
        	message = "Disabled account";
		} else if (e instanceof LockedException) {
			message = "Locked account";
		} else if (e instanceof CredentialsExpiredException) {
			message = "Token Expired";
		} else if (e instanceof AccountExpiredException) {
			message = "Account Expired";
		} else {
			message = e.getLocalizedMessage();
		}
        OpAck<User> payload = new OpAck<User>(OpAck.AckStatus.FAILED, message, null, "login");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(400);
        mapper.writeValue(response.getWriter(), payload);           
	}

}