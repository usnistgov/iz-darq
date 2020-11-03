package gov.nist.healthcare.iz.darq.access.domain;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Document(collection = "privilege")
public class UserRole implements GrantedAuthority {

    @Id()
    String id;
    String role;
    Set<Permission> permissions;

    public UserRole() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String getAuthority() {
        return role;
    }
}
