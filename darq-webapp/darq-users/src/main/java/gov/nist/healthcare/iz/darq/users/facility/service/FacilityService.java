package gov.nist.healthcare.iz.darq.service;

import gov.nist.healthcare.iz.darq.model.Facility;
import gov.nist.healthcare.iz.darq.model.FacilityDescriptor;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import gov.nist.healthcare.iz.darq.users.domain.User;

import java.util.List;

public interface FacilityService {

    List<FacilityDescriptor> getFacilities();
    Facility getFacilityById(String id) throws NotFoundException;
    Facility save(Facility facility) throws NotFoundException, OperationFailureException;
    Facility addMember(String id, String username) throws NotFoundException, OperationFailureException;
    Facility removeMember(String id, String username) throws NotFoundException, OperationFailureException;
    List<FacilityDescriptor> getUserFacilities(User user);
    boolean canSeeFacility(String facilityId, User user) throws NotFoundException;
}
