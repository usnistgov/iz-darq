package gov.nist.healthcare.iz.darq.access.security;

import gov.nist.healthcare.iz.darq.access.domain.*;
import gov.nist.healthcare.iz.darq.access.domain.exception.ResourceAccessForbidden;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.users.domain.User;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class CustomSecurityExpressionRoot extends SecurityExpressionRoot {

    private final SimpleResourceQualifier resourceQualifier;
    public static final String RESOURCE_ATTRIBUTE = "SECURITY_RESOLVED_RESOURCE";
    public static final String RESOURCE_SET_ATTRIBUTE = "SECURITY_RESOLVED_RESOURCE_SET";

    //Permissions
    public final Permission CONFIG_PUBLIC_VIEW = Permission.CONFIG_PUBLIC_VIEW;
    public final Permission CONFIG_PRIVATE_AUTHOR = Permission.CONFIG_PRIVATE_AUTHOR;
    public final Permission CONFIG_PUBLIC_AUTHOR = Permission.CONFIG_PUBLIC_AUTHOR;
    public final Permission CONFIG_PUBLISH = Permission.CONFIG_PUBLISH;
    public final Permission RT_PUBLIC_VIEW = Permission.RT_PUBLIC_VIEW;
    public final Permission RT_PRIVATE_AUTHOR = Permission.RT_PRIVATE_AUTHOR;
    public final Permission RT_PUBLIC_AUTHOR = Permission.RT_PUBLIC_AUTHOR;
    public final Permission RT_PUBLISH = Permission.RT_PUBLISH;
    public final Permission DATA_PRIVATE_AUTHOR = Permission.DATA_PRIVATE_AUTHOR;
    public final Permission DATA_FACILITY_VIEW = Permission.DATA_FACILITY_VIEW;
    public final Permission DATA_FACILITY_UPLOAD = Permission.DATA_FACILITY_UPLOAD;
    public final Permission DATA_FACILITY_AUTHOR = Permission.DATA_FACILITY_AUTHOR;
    public final Permission DATA_FACILITY_PUBLISH = Permission.DATA_FACILITY_PUBLISH;

    // ResourceType
    public final ResourceType ADF = ResourceType.ADF;
    public final ResourceType CONFIGURATION = ResourceType.CONFIGURATION;
    public final ResourceType REPORT_TEMPLATE = ResourceType.REPORT_TEMPLATE;
    public final ResourceType ANALYSIS_JOB = ResourceType.ANALYSIS_JOB;
    public final ResourceType REPORT = ResourceType.REPORT;
    public final ResourceType QUERY = ResourceType.QUERY;
    // Action
    public final Action VIEW = Action.VIEW;
    public final Action CREATE = Action.CREATE;
    public final Action CLONE = Action.CLONE;

    public final Action EDIT = Action.EDIT;
    public final Action DELETE = Action.DELETE;
    public final Action PUBLISH = Action.PUBLISH;
    public final Action UPLOAD = Action.UPLOAD;
    public final Action COMMENT = Action.COMMENT;
    public final Action ANALYSE = Action.ANALYSE;

    //Scope
    public final QualifiedScope GLOBAL = new QualifiedScope(Scope.GLOBAL);
    //Any
    public final QualifiedAccessToken ANY = new QualifiedAccessToken(AccessToken.ANY);


    public CustomSecurityExpressionRoot(Authentication authentication, SimpleResourceQualifier resourceQualifier) {
        super(authentication);
        this.resourceQualifier = resourceQualifier;
    }

    public User getPrincipal() {
        return (User) this.authentication.getPrincipal();
    }

    public boolean AccessResource(HttpServletRequest request, ResourceType resourceType, Set<Action> action, String id) throws NotFoundException, ResourceAccessForbidden {
        User user = this.getPrincipal();
        ResourceSecurityWrapper securityWrapper = this.resourceQualifier.getSecurityQualifiedResource(resourceType, id);
        boolean allow = user.getPermissions().hasActionsFor(securityWrapper.getScope(), resourceType, securityWrapper.getAccessToken(), action);
        if(allow) {
            this.setResourceAttribute(request, securityWrapper.getPayload());
            return true;
        } else {
            throw new ResourceAccessForbidden(resourceType, id, action);
        }
    }

    public boolean AccessMultipleResource(HttpServletRequest request, ResourceType resourceType, Set<Action> action, Set<String> ids) throws NotFoundException, ResourceAccessForbidden {
        User user = this.getPrincipal();
        Set<Object> resources = new HashSet<>();
        for(String id: ids) {
            ResourceSecurityWrapper securityWrapper = this.resourceQualifier.getSecurityQualifiedResource(resourceType, id);
            boolean allow = user.getPermissions().hasActionsFor(securityWrapper.getScope(), resourceType, securityWrapper.getAccessToken(), action);
            if(allow) {
                resources.add(securityWrapper.getPayload());
            } else {
                throw new ResourceAccessForbidden(resourceType, id, action);
            }
        }

        this.setResourceCollectionAttribute(request, resources);
        return true;
    }

    public boolean AccessResource(ResourceType resourceType, Set<Action> action, String id) throws NotFoundException, ResourceAccessForbidden {
        User user = this.getPrincipal();
        ResourceSecurityWrapper securityWrapper = this.resourceQualifier.getSecurityQualifiedResource(resourceType, id);
        boolean allow = user.getPermissions().hasActionsFor(securityWrapper.getScope(), resourceType, securityWrapper.getAccessToken(), action);
        if(allow) {
            return true;
        } else {
            throw new ResourceAccessForbidden(resourceType, id, action);
        }
    }

    public boolean AccessOperation(ResourceType resourceType, Action action, QualifiedScope scope) throws ResourceAccessForbidden {
        User user = this.getPrincipal();
        boolean allow = user.getPermissions().hasActionFor(scope, resourceType, ANY, action);
        if(allow) {
            return true;
        } else {
            throw new ResourceAccessForbidden(resourceType, action);
        }
    }

    public boolean AccessOperation(ResourceType resourceType, Action action, QualifiedScope scope, QualifiedAccessToken accessToken) throws ResourceAccessForbidden {
        User user = this.getPrincipal();
        boolean allow = user.getPermissions().hasActionFor(scope, resourceType, accessToken, action);
        if(allow) {
            return true;
        } else {
            throw new ResourceAccessForbidden(resourceType, action);
        }
    }

    public boolean hasPermission(String name) {
        try {
            Permission permission = Permission.valueOf(name);
            return this.getPrincipal().getPermissions().hasPermissions(Collections.singleton(permission));
        } catch (Exception e) {
          return false;
        }
    }

    public boolean isAdmin() {
        return this.getPrincipal().isAdministrator();
    }

    public QualifiedScope FACILITY(String qualifier) {
        return new QualifiedScope(Scope.FACILITY, qualifier);
    }
    public QualifiedAccessToken OWNED() {
        return new QualifiedAccessToken(AccessToken.OWNER, this.getPrincipal().getId());
    }
    public QualifiedAccessToken PUBLIC() {
        return new QualifiedAccessToken(AccessToken.PUBLIC);
    }

    public void setResourceAttribute(HttpServletRequest request, Object resource) {
        request.setAttribute(RESOURCE_ATTRIBUTE, resource);
    }

    public void setResourceCollectionAttribute(HttpServletRequest request, Set<Object> resources) {
        request.setAttribute(RESOURCE_SET_ATTRIBUTE, resources);
    }
}
