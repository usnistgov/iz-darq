package gov.nist.healthcare.iz.darq.access;

import gov.nist.healthcare.iz.darq.access.domain.Action;
import gov.nist.healthcare.iz.darq.access.domain.ResourceSecurityWrapper;
import gov.nist.healthcare.iz.darq.access.domain.ResourceType;
import gov.nist.healthcare.iz.darq.access.domain.exception.ResourceAccessForbidden;
import gov.nist.healthcare.iz.darq.access.security.SimpleResourceQualifier;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.users.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AccessService {

	@Autowired
	SimpleResourceQualifier resourceQualifier;

	public Set<Object> getResourcesByUserAccess(ResourceType resourceType, Set<String> ids, Set<Action> action, User user) throws NotFoundException, ResourceAccessForbidden {
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
		return resources;
	}

	public Object getResourceByUserAccess(ResourceType resourceType, String id, Set<Action> action, User user) throws NotFoundException, ResourceAccessForbidden {
		ResourceSecurityWrapper securityWrapper = this.resourceQualifier.getSecurityQualifiedResource(resourceType, id);
		boolean allow = user.getPermissions().hasActionsFor(securityWrapper.getScope(), resourceType, securityWrapper.getAccessToken(), action);
		if(allow) {
			return securityWrapper.getPayload();
		} else {
			throw new ResourceAccessForbidden(resourceType, id, action);
		}
	}
}
