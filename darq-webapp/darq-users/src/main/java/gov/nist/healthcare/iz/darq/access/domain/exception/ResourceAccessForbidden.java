package gov.nist.healthcare.iz.darq.access.domain.permission;

import gov.nist.healthcare.iz.darq.access.domain.Action;
import gov.nist.healthcare.iz.darq.access.domain.ResourceType;

import java.util.Collections;
import java.util.Set;

public class ResourceAccessForbidden extends Exception {
    private final ResourceType resourceType;
    private final String id;
    private final Set<Action> actions;

    public ResourceAccessForbidden(ResourceType resourceType, String id, Set<Action> actions) {
        super("actions " + actions.toString() + " forbidden for user on resource "+ resourceType + " with ID " + id);
        this.resourceType = resourceType;
        this.id = id;
        this.actions = actions;
    }

    public ResourceAccessForbidden(ResourceType resourceType, Action action) {
        super("action " + action.name() + " forbidden for user on resource type "+ resourceType);
        this.resourceType = resourceType;
        this.id = null;
        this.actions = Collections.singleton(action);
    }


    public ResourceType getResourceType() {
        return resourceType;
    }

    public String getId() {
        return id;
    }

    public Set<Action> getActions() {
        return actions;
    }
}
