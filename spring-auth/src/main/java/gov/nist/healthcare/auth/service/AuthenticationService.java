package gov.nist.healthcare.auth.service;

import gov.nist.healthcare.auth.domain.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

public interface AuthenticationTokenService {
    AbstractAuthenticationToken validateTokenAndGetPrincipal(Jws<Claims> token) throws AuthenticationException;
    Claims jwtClaims(Account account, Claims claims);
    <T> T getAuthenticationPrincipalAs(Class<T> clazz);
}
