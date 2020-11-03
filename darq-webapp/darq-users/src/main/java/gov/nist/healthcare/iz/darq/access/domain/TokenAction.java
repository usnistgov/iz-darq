package gov.nist.healthcare.iz.darq.access.domain.permission;

import gov.nist.healthcare.iz.darq.access.domain.Action;

public class TokenAction {
    private final AccessToken token;
    private final Action[] actions;

    public TokenAction(AccessToken token, Action... actions) {
        this.token = token;
        this.actions = actions;
    }

    public AccessToken getToken() {
        return token;
    }

    public Action[] getActions() {
        return actions;
    }
}
