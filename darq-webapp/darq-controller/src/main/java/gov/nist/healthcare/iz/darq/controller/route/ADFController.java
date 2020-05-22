package gov.nist.healthcare.iz.darq.controller.route;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Strings;
import gov.nist.healthcare.iz.darq.model.FacilityDescriptor;
import gov.nist.healthcare.iz.darq.service.FacilityService;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.model.UserUploadedFile;
import gov.nist.healthcare.iz.darq.repository.ADFMetaDataRepository;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.service.AccountService;
import gov.nist.healthcare.iz.darq.adf.service.ADFStore;
import gov.nist.healthcare.iz.darq.adf.service.ADFStoreUploadHandler;
import gov.nist.healthcare.iz.darq.controller.domain.ADFDescriptor;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.domain.OpAck.AckStatus;
import gov.nist.healthcare.iz.darq.model.DigestConfiguration;
import gov.nist.healthcare.iz.darq.repository.DigestConfigurationRepository;
import gov.nist.healthcare.iz.darq.service.utils.ConfigurationService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ADFController {

	@Autowired
	private ADFStoreUploadHandler uploadHandler;
	@Autowired
	private AccountService accountService;
	@Autowired
	private ADFMetaDataRepository repo;
	@Autowired
	private ConfigurationService configService;
	@Autowired
	private DigestConfigurationRepository confRepo;
	@Autowired
	private ADFStore storage;
	@Autowired
	private FacilityService facilityService;

	//  Facilities for user
	@RequestMapping(value = "/adf/facilities", method = RequestMethod.GET)
	@ResponseBody
	public List<FacilityDescriptor> forUser() throws NotFoundException, OperationFailureException {
		Account account = this.accountService.getCurrentUser();
		return this.facilityService.getUserFacilities(account);
	}
    
	@RequestMapping(value="/adf/upload", method=RequestMethod.POST)
	@ResponseBody
	public OpAck create(
			@RequestParam("file") MultipartFile file,
			@RequestParam("name") String name,
			@RequestParam(value = "facility", required = false) String facility
	) throws Exception{
		Account a = this.accountService.getCurrentUser();
		InputStream stream = file.getInputStream();
		long size = file.getSize();
		uploadHandler.handle(name, facility, stream, a.getUsername(), size);
		return new OpAck(AckStatus.SUCCESS, "ADF Uploaded", null, "upload");
	}
	
	@RequestMapping(value="/adf/facility/{facilityId}", method=RequestMethod.GET)
	@ResponseBody
	public List<ADFDescriptor> listFacility(@PathVariable("facilityId") String facilityId, final HttpServletRequest request) throws Exception {
		Account a = this.accountService.getCurrentUser();
		if(this.facilityService.canSeeFacility(facilityId, a)) {
			List<UserUploadedFile> adf = repo.findByFacilityId(facilityId);
			List<ADFDescriptor> result = new ArrayList<>();
			List<DigestConfiguration> configurations = this.confRepo.findAccessible(a.getUsername());
			for(UserUploadedFile md : adf){
				result.add(new ADFDescriptor(md, this.configService.compatibilities(md.getConfiguration(), configurations, a.getUsername())));
			}
			return result;
		} else {
			throw new OperationFailureException("User does not have access to this facility");
		}

	}

	@RequestMapping(value="/adf", method=RequestMethod.GET)
	@ResponseBody
	public List<ADFDescriptor> list(final HttpServletRequest request) throws Exception {
		Account a = this.accountService.getCurrentUser();
		System.out.println(a.getUsername());
		List<UserUploadedFile> adf = repo.findByOwnerAndFacilityIdIsNull(a.getUsername());
		List<ADFDescriptor> result = new ArrayList<>();
		List<DigestConfiguration> configurations = this.confRepo.findAccessible(a.getUsername());
		for(UserUploadedFile md : adf){
			result.add(new ADFDescriptor(md, this.configService.compatibilities(md.getConfiguration(), configurations, a.getUsername())));
		}
		return result;
	}
	
	@RequestMapping(value="/adf/{id}", method=RequestMethod.GET)
	@ResponseBody
	public UserUploadedFile get(@PathVariable("id") String id, final HttpServletRequest request) throws Exception {
		Account a = this.accountService.getCurrentUser();
		UserUploadedFile adf = repo.findOne(id);
		if(adf != null && (adf.getOwner().equals(a.getUsername()) || this.facilityService.canSeeFacility(adf.getFacilityId(), a))) {
			return adf;
		} else {
			throw new NotFoundException("ADF File "+id+" Not Found");
		}
	}
	
	@RequestMapping(value="/adf/{id}", method=RequestMethod.DELETE)
	@ResponseBody
	public OpAck<UserUploadedFile> delete(@PathVariable("id") String id, final HttpServletRequest request) throws Exception {
		Account a = this.accountService.getCurrentUser();
		boolean deleted = storage.delete(id, a.getUsername());
		if(deleted) {
			return new OpAck<UserUploadedFile>(AckStatus.SUCCESS, "File Deleted Successfully", null, "adf-delete");
		} else {
			throw new NotFoundException("ADF File "+id+" Not Found");
		}
	}

}