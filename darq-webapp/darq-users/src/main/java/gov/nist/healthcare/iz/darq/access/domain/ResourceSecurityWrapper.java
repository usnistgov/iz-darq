package gov.nist.healthcare.iz.darq.access.domain.permission;

import gov.nist.healthcare.iz.darq.access.domain.ResourceType;

public class ResourceSecurityWrapper {
    private final ResourceType type;
    private final QualifiedAccessToken accessToken;
    private final QualifiedScope scope;
    private final Object payload;

    public ResourceSecurityWrapper(ResourceType type, QualifiedAccessToken accessToken, QualifiedScope scope, Object payload) {
        this.type = type;
        this.accessToken = accessToken;
        this.scope = scope;
        this.payload = payload;
    }

    public ResourceType getType() {
        return type;
    }

    public QualifiedAccessToken getAccessToken() {
        return accessToken;
    }

    public QualifiedScope getScope() {
        return scope;
    }

    public Object getPayload() {
        return payload;
    }
}
