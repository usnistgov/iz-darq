package gov.nist.healthcare.iz.darq.users.domain;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.iz.darq.access.domain.UserRole;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "account")
public class UserAccount extends Account<UserRole> {
    private String fullName;
    private String organization;
    private Boolean signedConfidentialityAgreement = false;
    private boolean verified;
    @Indexed(unique = true)
    private String email;
    private boolean qDarAccount;
    private String source;
    private Set<IssuerId> issuerIdList;

    public Set<IssuerId> getIssuerIdList() {
        return issuerIdList;
    }

    public void setIssuerIdList(Set<IssuerId> issuerIdList) {
        this.issuerIdList = issuerIdList;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isqDarAccount() {
        return qDarAccount;
    }

    public void setqDarAccount(boolean qDarAccount) {
        this.qDarAccount = qDarAccount;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

}
