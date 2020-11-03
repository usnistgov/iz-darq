package gov.nist.healthcare.iz.darq.access.domain.permission;

import com.google.common.base.Strings;

import java.util.Objects;

public class QualifiedScope {
    private final Scope scope;
    private final String qualifier;

    public QualifiedScope(Scope scope, String qualifier) {
        this.scope = scope;
        this.qualifier = qualifier;
    }

    public QualifiedScope(Scope scope) {
        this.scope = scope;
        this.qualifier = null;
    }

    public Scope getScope() {
        return scope;
    }

    public String getQualifier() {
        return qualifier;
    }

    @Override
    public String toString() {
        return scope + (Strings.isNullOrEmpty(this.getQualifier()) ? "" : "_" + this.getQualifier());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QualifiedScope that = (QualifiedScope) o;
        return scope == that.scope &&
                Objects.equals(qualifier, that.qualifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scope, qualifier);
    }
}
