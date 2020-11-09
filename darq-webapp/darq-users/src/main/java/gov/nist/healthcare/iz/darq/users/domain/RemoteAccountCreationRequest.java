package gov.nist.healthcare.iz.darq.users.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class RemoteAccountCreationRequest {
    private String issuer;
    private String issuerId;
    private String email;
    private String fullName;
    private String organization;

    public RemoteAccountCreationRequest(String issuer, String issuerId, String email, String fullName, String organization) {
        this.issuer = issuer;
        this.issuerId = issuerId;
        this.email = email;
        this.fullName = fullName;
        this.organization = organization;
    }

    String trim(String str) {
        if(str != null) {
            return str.trim();
        }
        return null;
    }

    public UserAccount toAccount() {
        UserAccount account = new UserAccount();
        account.setEmail(trim(email));
        account.setFullName(trim(fullName));
        account.setOrganization(trim(organization));
        account.setqDarAccount(false);
        account.setSource(trim(issuer));
        account.setIssuerIdList(new HashSet<>(
                Collections.singletonList(new IssuerId(trim(issuer), trim(issuerId)))
        ));
        return  account;
    }


    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getIssuerId() {
        return issuerId;
    }

    public void setIssuerId(String issuerId) {
        this.issuerId = issuerId;
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

}
