package gov.nist.healthcare.iz.darq.access.domain.permission;

public class ScopeResourceTokenAction {
    private final Scope scope;
    private final ResourceTokenAction[] actions;

    public ScopeResourceTokenAction(Scope scope, ResourceTokenAction... actions) {
        this.scope = scope;
        this.actions = actions;
    }

    public Scope getScope() {
        return scope;
    }

    public ResourceTokenAction[] getActions() {
        return actions;
    }
}
