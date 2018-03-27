package gov.nist.healthcare.iz.darq.controller.route;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.immregistries.dqa.validator.issue.Detection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.service.AccountService;
import gov.nist.healthcare.iz.darq.adf.service.ADFStore;
import gov.nist.healthcare.iz.darq.adf.service.ADFStoreUploadHandler;
import gov.nist.healthcare.iz.darq.controller.domain.ADFDescriptor;
import gov.nist.healthcare.iz.darq.controller.domain.OpAck;
import gov.nist.healthcare.iz.darq.controller.domain.OpAck.AckStatus;
import gov.nist.healthcare.iz.darq.digest.domain.ADFMetaData;
import gov.nist.healthcare.iz.darq.model.ConfigurationDescriptor;
import gov.nist.healthcare.iz.darq.model.DigestConfiguration;
import gov.nist.healthcare.iz.darq.repository.ADFMetaDataRepository;
import gov.nist.healthcare.iz.darq.repository.DigestConfigurationRepository;
import gov.nist.healthcare.iz.darq.service.impl.ADFStorage;
import gov.nist.healthcare.iz.darq.service.utils.ConfigurationService;

@RestController
@RequestMapping("/api")
public class ConfigurationController {

	@Autowired
	private AccountService accountService;
	@Autowired
	private ADFMetaDataRepository repo;
	@Autowired
	private ConfigurationService configService;
	@Autowired
	private DigestConfigurationRepository confRepo;
	@Autowired
	private ObjectMapper mapper;
	
    @RequestMapping(value = "/configuration", method = RequestMethod.GET)
    @ResponseBody
    public List<ConfigurationDescriptor> all() {
    	Account a = this.accountService.getCurrentUser();
    	return confRepo.findAccessible(a.getUsername()).stream()
    	.map(x -> {
    		return this.configService.extract(x);
    	})
    	.collect(Collectors.toList());
    }
    
	@RequestMapping(value="/configuration", method=RequestMethod.POST)
	@ResponseBody
	public OpAck create(@RequestBody DigestConfiguration config, final HttpServletRequest request) throws Exception{
		Account a = this.accountService.getCurrentUser();
		if(config.getId() == null || config.getId().isEmpty()){
			config.setOwner(a.getUsername());
		}
		this.confRepo.save(config);
		return new OpAck(AckStatus.SUCCESS, config.getId(), "upload");
	}
	
	@RequestMapping(value="/configuration/{id}/download", method=RequestMethod.GET)
	public void download(@PathVariable("id") String id, final HttpServletResponse rsp) throws Exception {
		Account a = this.accountService.getCurrentUser();
		DigestConfiguration conf = confRepo.findOne(id);
		if(conf == null || !(conf.getOwner().equals(a.getUsername()) || conf.isPublished())){
			rsp.sendError(404, "Configuration "+id+" Not Found");
		}
		else {
			rsp.setContentType("application/json");
			rsp.setHeader("Content-disposition", "attachment;filename=configuration.json");
			rsp.getOutputStream().write(mapper.writeValueAsBytes(conf.getPayload()));
		}
	}
	
	@RequestMapping(value="/configuration/{id}", method=RequestMethod.GET)
	@ResponseBody
	public DigestConfiguration get(@PathVariable("id") String id, final HttpServletResponse rsp) throws Exception {
		Account a = this.accountService.getCurrentUser();
		DigestConfiguration conf = confRepo.findOne(id);
		if(conf == null || !(conf.getOwner().equals(a.getUsername()) || conf.isPublished())){
			rsp.sendError(404, "Configuration "+id+" Not Found");
			return null;
		}
		else {
			conf.setVonly(!a.getUsername().equals(conf.getOwner()));
			return conf;
		}
	}
	
	@RequestMapping(value="/configuration/{id}", method=RequestMethod.DELETE)
	@ResponseBody
	public OpAck delete(@PathVariable("id") String id, final HttpServletRequest request) throws Exception {
		Account a = this.accountService.getCurrentUser();
		DigestConfiguration x = confRepo.findByOwnerAndId(a.getUsername(), id);
		if(x != null){
			this.confRepo.delete(x);
			return new OpAck(AckStatus.SUCCESS, "", "config-delete");
		}
		else {
			return new OpAck(AckStatus.FAILURE, "Not Found", "config-delete");
		}
	}

}