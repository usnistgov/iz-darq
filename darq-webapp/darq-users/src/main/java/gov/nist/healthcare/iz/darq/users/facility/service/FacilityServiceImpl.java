package gov.nist.healthcare.iz.darq.service.impl;
import gov.nist.healthcare.iz.darq.model.Facility;
import gov.nist.healthcare.iz.darq.model.FacilityDescriptor;
import gov.nist.healthcare.iz.darq.repository.FacilityRepository;
import gov.nist.healthcare.iz.darq.service.FacilityService;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import gov.nist.healthcare.iz.darq.users.domain.User;
import gov.nist.healthcare.iz.darq.users.service.impl.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacilityServiceImpl implements FacilityService {

    @Autowired
    FacilityRepository facilityRepository;
    @Autowired
    UserManagementService userManagementService;

    @Override
    public List<FacilityDescriptor> getFacilities() {
        return this.facilityRepository.findAll().stream().map((f) -> new FacilityDescriptor(f.getId(), f.getName(), f.getDescription(), f.getMembers().size())).collect(Collectors.toList());
    }

    @Override
    public Facility getFacilityById(String id) throws NotFoundException {
        Facility facility = this.facilityRepository.findOne(id);
        if(facility == null) {
            throw new NotFoundException("Facility "+id+" not found");
        }
        return facility;
    }

    @Override
    public Facility save(Facility f) throws NotFoundException, OperationFailureException {
        this.getFacilityById(f.getId());
        boolean all = f.getMembers().stream().allMatch((m) -> this.userManagementService.exists(m));
        if(all) {
            return this.facilityRepository.save(f);
        } else {
            throw new OperationFailureException("One or more members not found");
        }
    }

    @Override
    public Facility addMember(String id, String username) throws NotFoundException, OperationFailureException {
        this.userManagementService.findUserByUsernameOrFail(username);
        Facility facility = this.getFacilityById(id);

        if(facility.getMembers().contains(username)) {
            throw new OperationFailureException("User "+username+" already member of facility");
        } else {
            facility.getMembers().add(username);
            return this.facilityRepository.save(facility);
        }
    }

    @Override
    public Facility removeMember(String id, String username) throws NotFoundException, OperationFailureException {
        this.userManagementService.findUserByUsernameOrFail(username);
        Facility facility = this.getFacilityById(id);

        if(!facility.getMembers().contains(username)) {
            throw new OperationFailureException("User "+username+" not member of facility");
        } else {
            facility.getMembers().remove(username);
            return this.facilityRepository.save(facility);
        }
    }

    @Override
    public List<FacilityDescriptor> getUserFacilities(User user) {
        return (user.isAdministrator() ?
                this.facilityRepository.findAll() :
                this.facilityRepository.findByMembersContaining(user.getUsername()))
        .stream().map((a) -> new FacilityDescriptor(a.getId(), a.getName(), a.getDescription(), a.getMembers().size())).collect(Collectors.toList());
    }

    @Override
    public boolean canSeeFacility(String facilityId, User user) throws NotFoundException {
        Facility f = this.getFacilityById(facilityId);
        return user.isAdministrator() || f.getMembers().stream().anyMatch((member) -> user.getUsername().equals(member));
    }


}
