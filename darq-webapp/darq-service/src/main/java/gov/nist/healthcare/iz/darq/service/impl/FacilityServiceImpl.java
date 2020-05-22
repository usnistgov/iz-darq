package gov.nist.healthcare.iz.darq.service.impl;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.repository.AccountRepository;
import gov.nist.healthcare.iz.darq.model.Facility;
import gov.nist.healthcare.iz.darq.model.FacilityDescriptor;
import gov.nist.healthcare.iz.darq.repository.FacilityRepository;
import gov.nist.healthcare.iz.darq.service.FacilityService;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacilityServiceImpl implements FacilityService {

    @Autowired
    FacilityRepository facilityRepository;
    @Autowired
    AccountRepository accountRepository;

    @Override
    public List<FacilityDescriptor> getFacilities() {
        return this.facilityRepository.findAll().stream().map((f) -> {
            return new FacilityDescriptor(f.getId(), f.getName(), f.getDescription(), f.getMembers().size());
        }).collect(Collectors.toList());
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
        Facility facility = this.getFacilityById(f.getId());
        if(facility == null) {
            throw new NotFoundException("Facility "+f.getId()+" not found");
        }
        boolean all = f.getMembers().stream().allMatch((m) -> this.accountRepository.findByUsername(m) != null);
        if(all) {
            return this.facilityRepository.save(f);
        } else {
            throw new OperationFailureException("One or more members not found");
        }
    }

    @Override
    public Facility addMember(String id, String username) throws NotFoundException, OperationFailureException {
        Facility facility = this.getFacilityById(id);
        if(facility == null) {
            throw new NotFoundException("Facility "+id+" not found");
        }

        Account user = this.accountRepository.findByUsername(username);
        if(user == null) {
            throw new NotFoundException("User "+username+" not found");
        }

        if(facility.getMembers().contains(username)) {
            throw new OperationFailureException("User "+username+" already member of facility");
        } else {
            facility.getMembers().add(username);
            return this.facilityRepository.save(facility);
        }
    }

    @Override
    public Facility removeMember(String id, String username) throws NotFoundException, OperationFailureException {
        Facility facility = this.getFacilityById(id);
        if(facility == null) {
            throw new NotFoundException("Facility "+id+" not found");
        }

        Account user = this.accountRepository.findByUsername(username);
        if(user == null) {
            throw new NotFoundException("User "+username+" not found");
        }

        if(!facility.getMembers().contains(username)) {
            throw new OperationFailureException("User "+username+" not member of facility");
        } else {
            facility.getMembers().remove(username);
            return this.facilityRepository.save(facility);
        }
    }

    @Override
    public List<FacilityDescriptor> getUserFacilities(Account user) {
        boolean isAdmin = user.getPrivileges().stream().anyMatch((p) -> p.getRole().equals("ADMIN"));
        return (isAdmin ?
                this.facilityRepository.findAll() :
                this.facilityRepository.findByMembersContaining(user.getUsername()))
        .stream().map((a) -> new FacilityDescriptor(a.getId(), a.getName(), a.getDescription(), a.getMembers().size())).collect(Collectors.toList());
    }

    @Override
    public boolean canSeeFacility(String facilityId, Account user) throws NotFoundException {
        Facility f = this.facilityRepository.findOne(facilityId);
        boolean isAdmin = user.getPrivileges().stream().anyMatch((p) -> p.getRole().equals("ADMIN"));
        if(f == null) {
            throw new NotFoundException("Facility "+ facilityId + " not found");
        } else {
            return isAdmin || f.getMembers().stream().anyMatch((member) -> user.getUsername().equals(member));
        }
    }
}
