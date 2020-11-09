package gov.nist.healthcare.iz.darq.users.domain;

public class IssuerId {
    String issuer;
    String userId;

    public IssuerId(String issuer, String userId) {
        this.issuer = issuer;
        this.userId = userId;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getUserId() {
        return userId;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
