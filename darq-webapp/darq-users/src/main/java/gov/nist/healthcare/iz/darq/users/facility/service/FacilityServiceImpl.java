package gov.nist.healthcare.iz.darq.users.facility.service;
import gov.nist.healthcare.iz.darq.model.Facility;
import gov.nist.healthcare.iz.darq.model.FacilityDescriptor;
import gov.nist.healthcare.iz.darq.repository.FacilityRepository;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import gov.nist.healthcare.iz.darq.users.domain.User;
import gov.nist.healthcare.iz.darq.users.service.impl.UserManagementService;
import java.util.List;
import java.util.stream.Collectors;

public class FacilityServiceImpl implements FacilityService {

    private final FacilityRepository facilityRepository;
    private final UserManagementService userManagementService;

    public FacilityServiceImpl(FacilityRepository facilityRepository, UserManagementService userManagementService) {
        this.facilityRepository = facilityRepository;
        this.userManagementService = userManagementService;
    }

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
        boolean all = f.getMembers().stream().allMatch(this.userManagementService::existsById);
        if(all) {
            return this.facilityRepository.save(f);
        } else {
            throw new OperationFailureException("One or more members not found");
        }
    }

    @Override
    public Facility findByName(String facility) {
        return this.facilityRepository.findByName(facility);
    }

    @Override
    public Facility addMember(String id, String userId) throws NotFoundException, OperationFailureException {
        User u = this.userManagementService.findUserByIdOrFail(userId);
        Facility facility = this.getFacilityById(id);

        if(facility.getMembers().contains(userId)) {
            throw new OperationFailureException("User "+u.getUsername()+" (" + userId + ") already member of facility");
        } else {
            facility.getMembers().add(userId);
            return this.facilityRepository.save(facility);
        }
    }

    @Override
    public Facility removeMember(String id, String userId) throws NotFoundException, OperationFailureException {
        User u = this.userManagementService.findUserByIdOrFail(userId);
        Facility facility = this.getFacilityById(id);

        if(!facility.getMembers().contains(userId)) {
            throw new OperationFailureException("User "+u.getUsername()+" (" + userId + ") not member of facility");
        } else {
            facility.getMembers().remove(userId);
            return this.facilityRepository.save(facility);
        }
    }

    @Override
    public List<FacilityDescriptor> getUserFacilities(User user) {
        return (user.isAdministrator() ?
                this.facilityRepository.findAll() :
                this.facilityRepository.findByMembersContaining(user.getId()))
        .stream().map((a) -> new FacilityDescriptor(a.getId(), a.getName(), a.getDescription(), a.getMembers().size())).collect(Collectors.toList());
    }

}
