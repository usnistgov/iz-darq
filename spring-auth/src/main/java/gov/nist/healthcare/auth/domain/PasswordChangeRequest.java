package gov.nist.healthcare.auth.domain;

public class PasswordChange {
    public String username;
    public String password;
    public String replacement;

    public PasswordChange() {
    }

    public PasswordChange(String username, String password, String replacement) {
        this.username = username;
        this.password = password;
        this.replacement = replacement;
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

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }
}
