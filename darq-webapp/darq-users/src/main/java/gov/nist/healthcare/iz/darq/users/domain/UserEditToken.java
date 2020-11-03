package gov.nist.healthcare.iz.darq.users.domain;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class UserEditToken {

    @Id
    private String id;
    private String userId;
    private UserEditTokenType type;
    private double issuedAt;
    private double duration;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserEditTokenType getType() {
        return type;
    }

    public void setType(UserEditTokenType type) {
        this.type = type;
    }

    public double getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(double issuedAt) {
        this.issuedAt = issuedAt;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
}
