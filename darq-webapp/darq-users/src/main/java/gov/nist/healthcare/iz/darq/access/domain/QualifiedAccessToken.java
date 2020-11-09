package gov.nist.healthcare.iz.darq.access.domain;

import com.google.common.base.Strings;
import gov.nist.healthcare.iz.darq.users.domain.User;

import java.util.Objects;

public class QualifiedAccessToken {
    public static final QualifiedAccessToken ANY = new QualifiedAccessToken(AccessToken.ANY);

    private final AccessToken token;
    private final String qualifier;

    public QualifiedAccessToken(AccessToken token, String qualifier) {
        this.token = token;
        this.qualifier = qualifier;
    }

    public QualifiedAccessToken(AccessToken token) {
        this.token = token;
        this.qualifier = null;
    }

    public QualifiedAccessToken(AccessToken token, User user) {
        this.token = token;
        switch (token) {
            case OWNER:
            case PARTICIPANT:
                this.qualifier = user.getId();
                break;
            case ANY:
            case PUBLIC:
            default:
                this.qualifier = null;
        }
    }

    public AccessToken getToken() {
        return token;
    }

    public String getQualifier() {
        return qualifier;
    }

    @Override
    public String toString() {
        return token + (Strings.isNullOrEmpty(this.getQualifier()) ? "" : "_" + this.getQualifier());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QualifiedAccessToken that = (QualifiedAccessToken) o;
        return token == that.token &&
                Objects.equals(qualifier, that.qualifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, qualifier);
    }
}
