package gov.nist.healthcare.iz.darq.users.domain;

public class ValidateTokenRequest {
    String token;
    UserEditTokenType context;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserEditTokenType getContext() {
        return context;
    }

    public void setContext(UserEditTokenType context) {
        this.context = context;
    }
}
