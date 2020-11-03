package gov.nist.healthcare.auth.domain;

import org.springframework.security.core.GrantedAuthority;

public abstract class Authority implements GrantedAuthority {
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return this.role;
    }
}
