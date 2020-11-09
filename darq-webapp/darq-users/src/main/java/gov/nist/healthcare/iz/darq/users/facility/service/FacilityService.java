package gov.nist.healthcare.iz.darq.users.facility.service;

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
    Facility findByName(String facility);
    Facility addMember(String id, String userId) throws NotFoundException, OperationFailureException;
    Facility removeMember(String id, String userId) throws NotFoundException, OperationFailureException;
    List<FacilityDescriptor> getUserFacilities(User user);
}
