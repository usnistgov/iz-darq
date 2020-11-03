package gov.nist.healthcare.iz.darq.users.domain;

import gov.nist.healthcare.iz.darq.access.domain.UserPermission;
import java.util.Set;

public class User {
    String id;
    String username;
    String name;
    String screenName;
    boolean credentials;
    boolean verified;
    String source;
    String email;
    String organization;
    Set<String> roles;
    UserPermission permissions;
    AccountStatus status;
    Set<IssuerId> issuerIdList;
    boolean administrator;

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isCredentials() {
        return credentials;
    }

    public void setCredentials(boolean credentials) {
        this.credentials = credentials;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public UserPermission getPermissions() {
        return permissions;
    }

    public void setPermissions(UserPermission permissions) {
        this.permissions = permissions;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public Set<IssuerId> getIssuerIdList() {
        return issuerIdList;
    }

    public void setIssuerIdList(Set<IssuerId> issuerIdList) {
        this.issuerIdList = issuerIdList;
    }
}
