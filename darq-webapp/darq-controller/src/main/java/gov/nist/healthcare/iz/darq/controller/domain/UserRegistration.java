package gov.nist.healthcare.iz.darq.controller.domain;

import gov.nist.healthcare.auth.domain.Account;

public class UserRegistration {

    private String username;
    private String password;
    private String email;
    private String fullName;
    private String organization;
    private Boolean signedConfidentialityAgreement = false;

    public Account toAccount() {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        account.setEmail(email);
        account.setFullName(fullName);
        account.setOrganization(organization);
        account.setSignedConfidentialityAgreement(signedConfidentialityAgreement);
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
