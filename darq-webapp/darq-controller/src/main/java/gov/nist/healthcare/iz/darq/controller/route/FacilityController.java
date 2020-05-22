package gov.nist.healthcare.iz.darq.controller.route;

import com.google.common.base.Strings;
import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.repository.AccountRepository;
import gov.nist.healthcare.auth.service.AccountService;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.controller.domain.FacilityMember;
import gov.nist.healthcare.iz.darq.model.ConfigurationDescriptor;
import gov.nist.healthcare.iz.darq.model.Facility;
import gov.nist.healthcare.iz.darq.model.FacilityDescriptor;
import gov.nist.healthcare.iz.darq.repository.FacilityRepository;
import gov.nist.healthcare.iz.darq.service.FacilityService;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/facility")
public class FacilityController {

    @Autowired
    FacilityRepository facilityRepository;

    @Autowired
    FacilityService facilityService;

    @Autowired
    AccountService accountService;


    //  Get All Facilities (Descriptor)
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public List<FacilityDescriptor> all() {
        return facilityService.getFacilities();
    }

    //  Get Facility By Id
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Facility byId(@PathVariable("id") String id) throws NotFoundException {
        return facilityService.getFacilityById(id);
    }

    //  Save Descriptor
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public OpAck<FacilityDescriptor> create(@RequestBody FacilityDescriptor descriptor) throws NotFoundException, OperationFailureException {
        if(Strings.isNullOrEmpty(descriptor.getId()) && !Strings.isNullOrEmpty(descriptor.getName())) {
            Facility f = this.facilityRepository.save(new Facility(descriptor.getId(), descriptor.getName(), descriptor.getDescription(), new HashSet<>()));
            return new OpAck<>(OpAck.AckStatus.SUCCESS, "Facility Created successfully", new FacilityDescriptor(f.getId(), f.getName(), f.getDescription(), f.getMembers().size()), "facility-create");
        }
        throw new OperationFailureException("Invalid Facility Info");
    }

    //  Save
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public OpAck<Facility> save(@RequestBody Facility facility) throws NotFoundException, OperationFailureException {
        Facility f = facilityService.save(facility);
        return new OpAck<>(OpAck.AckStatus.SUCCESS, "Facility saved successfully", f, "facility-save");
    }

    // Add Member
    @RequestMapping(value = "/{id}/add-member", method = RequestMethod.POST)
    @ResponseBody
    public OpAck<Facility> add(@PathVariable("id") String id, @RequestBody FacilityMember member) throws NotFoundException, OperationFailureException {
        Facility f = facilityService.addMember(id, member.getUsername());
        return new OpAck<>(OpAck.AckStatus.SUCCESS, "Member added successfully successfully", f, "facility-save");
    }

    // Remove Member
    @RequestMapping(value = "/{id}/remove-member", method = RequestMethod.POST)
    @ResponseBody
    public OpAck<Facility> remove(@PathVariable("id") String id, @RequestBody FacilityMember member) throws NotFoundException, OperationFailureException {
        Facility f = facilityService.removeMember(id, member.getUsername());
        return new OpAck<>(OpAck.AckStatus.SUCCESS, "Member removed successfully successfully", f, "facility-save");
    }

}
