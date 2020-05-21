package gov.nist.healthcare.iz.darq.controller.route;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import gov.nist.healthcare.iz.darq.controller.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.model.UserUploadedFile;
import gov.nist.healthcare.iz.darq.repository.ADFMetaDataRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;

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
	
	@RequestMapping(value="/adf", method=RequestMethod.GET)
	@ResponseBody
	public List<ADFDescriptor> list(final HttpServletRequest request) throws Exception {
		Account a = this.accountService.getCurrentUser();
		List<UserUploadedFile> adf = repo.findByOwner(a.getUsername());
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
		UserUploadedFile adf = repo.findByIdAndOwner(id, a.getUsername());
		if(adf != null) {
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