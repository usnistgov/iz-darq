package gov.nist.healthcare.iz.darq.auth.aart;

import gov.nist.healthcare.iz.darq.users.domain.UserAccount;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JWTAuthenticationAARTToken extends AbstractAuthenticationToken {
    private final UserAccount principal;
    private final Jws<Claims> credentials;
    private final String targetPath;

    public JWTAuthenticationAARTToken(UserAccount principal, Jws<Claims> credentials, String targetPath, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.targetPath = targetPath;
    }

    @Override
    public Jws<Claims> getCredentials() {
        return credentials;
    }

    @Override
    public UserAccount getPrincipal() {
        return principal;
    }

    public String getTargetPath() {
        return targetPath;
    }
}
