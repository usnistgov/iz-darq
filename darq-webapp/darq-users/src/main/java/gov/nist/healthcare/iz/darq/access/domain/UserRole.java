package gov.nist.healthcare.iz.darq.access.domain;
import gov.nist.healthcare.auth.domain.Authority;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.Objects;
import java.util.Set;

@Document(collection = "privilege")
public class UserRole extends Authority {

    @Id()
    String id;
    Set<Permission> permissions;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRole userRole = (UserRole) o;
        return id.equals(userRole.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
