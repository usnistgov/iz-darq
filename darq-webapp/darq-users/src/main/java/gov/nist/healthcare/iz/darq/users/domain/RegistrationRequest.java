package gov.nist.healthcare.iz.darq.users.domain;

public class RegistrationRequest {

    private String username;
    private String password;
    private String email;
    private String fullName;
    private String organization;
    private Boolean signedConfidentialityAgreement = false;

    String trim(String str) {
        if(str != null) {
            return str.trim();
        }
        return null;
    }

    String lowercase(String str) {
        if(str != null) {
            return str.toLowerCase();
        }
        return null;
    }

    String sanitize(String str) {
        return lowercase(trim(str));
    }
    
    public UserAccount toAccount() {
        UserAccount account = new UserAccount();
        account.setUsername(username);
        account.setPassword(password);
        account.setEmail(lowercase(email));
        account.setFullName(trim(fullName));
        account.setOrganization(trim(organization));
        account.setSignedConfidentialityAgreement(signedConfidentialityAgreement);
        account.setPending(true);
        account.setqDarAccount(true);
        return  account;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public Boolean getSignedConfidentialityAgreement() {
        return signedConfidentialityAgreement;
    }

    public void setSignedConfidentialityAgreement(Boolean signedConfidentialityAgreement) {
        this.signedConfidentialityAgreement = signedConfidentialityAgreement;
    }
}
