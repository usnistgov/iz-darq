package gov.nist.healthcare.iz.darq.access.domain.permission;

import gov.nist.healthcare.iz.darq.access.domain.ResourceType;

public class ResourceTokenAction {
    private final ResourceType resourceType;
    private final TokenAction[] tokenActions;

    public ResourceTokenAction(ResourceType resourceType, TokenAction... tokenActions) {
        this.resourceType = resourceType;
        this.tokenActions = tokenActions;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public TokenAction[] getTokenActions() {
        return tokenActions;
    }
}
