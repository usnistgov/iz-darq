package gov.nist.healthcare.auth.service;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.domain.Authority;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;

public interface AuthenticationService<E extends Account<P>, P extends Authority, Pr> {
    AbstractAuthenticationToken validateTokenAndGetPrincipal(Jws<Claims> token) throws AuthenticationException;
    void verifyAccountAndHandleLoginResponse(HttpServletRequest request, HttpServletResponse response, E account) throws IOException, ServletException, InvalidKeySpecException, NoSuchAlgorithmException;
    Cookie createAuthCookie(E account, Date expiresAt, ArrayList<String> facilities) throws Exception;
    Cookie createAuthCookieWithDefaultDuration(E account, ArrayList<String> facilities) throws Exception;
    void clearLoginCookie(HttpServletResponse response);
    Pr verifyAccountAndCreatePrincipal(E account, ArrayList<String> facilities) throws AuthenticationException;
    Pr login(HttpServletRequest request, HttpServletResponse response, E account) throws Exception;
    Claims jwtClaims(E account, Claims claims);
}
