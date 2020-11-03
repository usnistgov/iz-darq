package gov.nist.healthcare.iz.darq.access.method.security;

import gov.nist.healthcare.iz.darq.access.domain.Action;
import gov.nist.healthcare.iz.darq.access.domain.ResourceType;
import gov.nist.healthcare.iz.darq.access.domain.Scope;
import gov.nist.healthcare.iz.darq.access.service.AccessControlService;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;


public class CustomMethodSecurityExpressionRoot<T> extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {
    private Object filterObject;
    private Object returnObject;
    private final AccessControlService<T> accessControlService;

    public CustomMethodSecurityExpressionRoot(Authentication authentication, AccessControlService accessControlService) {
        super(authentication);
        this.accessControlService = accessControlService;
    }

    @Override
    public T getPrincipal() {
        return (T) this.authentication.getPrincipal();
    }

    public boolean hasPermission(ResourceType resourceType, Action action, Object resource) {
        return this.accessControlService.allowed(resourceType, action, resource, this.getPrincipal());
    }

    public boolean hasPermission(ResourceType resourceType, Action action, String resourceId) {
        return this.accessControlService.allowed(resourceType, action, resourceId, this.getPrincipal());
    }

    public boolean hasPermission(ResourceType resourceType, Action action, Scope scope) {
        return this.accessControlService.allowed(resourceType, action, scope, this.getPrincipal());
    }

    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getReturnObject() {
        return returnObject;
    }

    @Override
    public Object getThis() {
        return this;
    }
}