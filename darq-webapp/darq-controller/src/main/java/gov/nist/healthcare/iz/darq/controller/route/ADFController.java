package gov.nist.healthcare.iz.darq.controller.route;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.service.AccountService;
import gov.nist.healthcare.iz.darq.adf.service.ADFStore;
import gov.nist.healthcare.iz.darq.adf.service.ADFStoreUploadHandler;
import gov.nist.healthcare.iz.darq.controller.domain.ADFDescriptor;
import gov.nist.healthcare.iz.darq.controller.domain.OpAck;
import gov.nist.healthcare.iz.darq.controller.domain.OpAck.AckStatus;
import gov.nist.healthcare.iz.darq.digest.domain.ADFMetaData;
import gov.nist.healthcare.iz.darq.model.DigestConfiguration;
import gov.nist.healthcare.iz.darq.repository.ADFMetaDataRepository;
import gov.nist.healthcare.iz.darq.repository.DigestConfigurationRepository;
import gov.nist.healthcare.iz.darq.service.utils.ConfigurationService;

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
	public OpAck create(final HttpServletRequest request) throws Exception{
		Account a = this.accountService.getCurrentUser();
		InputStream stream = null;
		String name = null;
		long size = 0;
		for(Part p : request.getParts()){
			if(p.getName().equals("name")){
				name = IOUtils.toString(p.getInputStream(), Charset.forName("UTF-8")); 
			}
			if(p.getName().equals("file")){
				stream = p.getInputStream();
				size = p.getSize();
			}
		}
		uploadHandler.handle(name, stream, a.getUsername(), size);
		return new OpAck(AckStatus.SUCCESS, "ADF Uploaded", "upload");
	}
	
	@RequestMapping(value="/adf", method=RequestMethod.GET)
	@ResponseBody
	public List<ADFDescriptor> list(final HttpServletRequest request) throws Exception{
		Account a = this.accountService.getCurrentUser();
		List<ADFMetaData> adf = repo.findByOwner(a.getUsername());
		List<ADFDescriptor> result = new ArrayList<>();
		List<DigestConfiguration> configurations = this.confRepo.findAccessible(a.getUsername());
		for(ADFMetaData md : adf){
			result.add(new ADFDescriptor(md, this.configService.compatibilities(md.getConfiguration(), configurations)));
		}
		return result;
	}
	
	@RequestMapping(value="/adf/{id}", method=RequestMethod.GET)
	@ResponseBody
	public ADFMetaData get(@PathVariable("id") String id, final HttpServletRequest request) throws Exception {
		Account a = this.accountService.getCurrentUser();
		return repo.findByIdAndOwner(id, a.getUsername());
	}
	
	@RequestMapping(value="/adf/{id}", method=RequestMethod.DELETE)
	@ResponseBody
	public OpAck delete(@PathVariable("id") String id, final HttpServletRequest request) throws Exception {
		Account a = this.accountService.getCurrentUser();
		boolean x = storage.delete(id, a.getUsername());
		return new OpAck(x ? AckStatus.SUCCESS : AckStatus.FAILURE, "", "adf-delete");
	}

}