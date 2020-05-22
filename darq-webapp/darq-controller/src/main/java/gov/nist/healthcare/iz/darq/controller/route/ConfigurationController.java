package gov.nist.healthcare.iz.darq.controller.route;


import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
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
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.domain.OpAck.AckStatus;
import gov.nist.healthcare.iz.darq.model.ConfigurationDescriptor;
import gov.nist.healthcare.iz.darq.model.DigestConfiguration;
import gov.nist.healthcare.iz.darq.repository.DigestConfigurationRepository;
import gov.nist.healthcare.iz.darq.service.utils.ConfigurationService;

@RestController
@RequestMapping("/api/configuration")
public class ConfigurationController {

	@Autowired
	private AccountService accountService;
	@Autowired
	private ConfigurationService configService;
	@Autowired
	private DigestConfigurationRepository confRepo;
	@Autowired
	private ObjectMapper mapper;

	//  Get All Accessible Configuration (Descriptor)
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public List<ConfigurationDescriptor> all() {
    	Account a = this.accountService.getCurrentUser();
    	return confRepo.findAccessible(a.getUsername()).stream()
    	.map(x -> this.configService.extract(x, a.getUsername()))
    	.collect(Collectors.toList());
    }

	//  Get Configuration by Id (Owned or Published [viewOnly])
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	@ResponseBody
	public DigestConfiguration get(@PathVariable("id") String id) throws NotFoundException {
		Account a = this.accountService.getCurrentUser();
		DigestConfiguration conf = confRepo.findOne(id);
		if(conf == null){
			throw new NotFoundException("Configuration "+id+" Not Found");
		}
		else if(!(conf.getOwner().equals(a.getUsername())) && !conf.isPublished()) {
			throw new NotFoundException("Configuration "+id+" Not Found");
		}
		else {
			conf.setViewOnly(!a.getUsername().equals(conf.getOwner()) || conf.isLocked());
			return conf;
		}
	}

	//  Clone Report Template by Id (Owned or Published)
	@RequestMapping(value="/{id}/clone", method=RequestMethod.POST)
	@ResponseBody
	public OpAck<DigestConfiguration> clone(@PathVariable("id") String id, final HttpServletResponse rsp) throws Exception {
		Account a = this.accountService.getCurrentUser();
		DigestConfiguration conf = confRepo.findOne(id);
		if(conf == null){
			throw new NotFoundException("Configuration "+id+" Not Found");
		}
		else if(!(conf.getOwner().equals(a.getUsername())) && !conf.isPublished()) {
			throw new NotFoundException("Configuration "+id+" Not Found");
		}
		else {
			conf.setId(null);
			conf.setName("[Clone] "+conf.getName());
			conf.setOwner(a.getUsername());
			conf.setPublished(false);
			conf.setLocked(false);
			DigestConfiguration saved = this.confRepo.save(conf);
			conf.setViewOnly(!a.getUsername().equals(conf.getOwner()) || conf.isLocked());
			return new OpAck<>(AckStatus.SUCCESS, "Configuration Successfully Cloned", saved, "config-clone");
		}
	}


	//  Delete Configuration by Id (Owned)
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	@ResponseBody
	public OpAck<DigestConfiguration> delete(@PathVariable("id") String id) throws NotFoundException {
		Account a = this.accountService.getCurrentUser();
		DigestConfiguration x = confRepo.findByOwnerAndId(a.getUsername(), id);
		if(x != null){
			this.confRepo.delete(x);
			return new OpAck<>(AckStatus.SUCCESS, "Configuration Successfully Deleted", null,"config-delete");
		}
		else {
			throw new NotFoundException("Configuration "+id+" Not Found");
		}
	}

	//  Save Configuration (Owned and not Locked or New)
	@RequestMapping(value="/", method=RequestMethod.POST)
	@ResponseBody
	public OpAck<DigestConfiguration> create(@RequestBody DigestConfiguration config) throws OperationFailureException {
		Account a = this.accountService.getCurrentUser();
		if(config.getId() == null || config.getId().isEmpty()){
			config.setOwner(a.getUsername());
		} else {
			DigestConfiguration existing = this.confRepo.findByOwnerAndId(a.getUsername(), config.getId());
			if(existing == null) {
				throw new OperationFailureException("Can't save configuration "+config.getId());
			}

			if(existing.isLocked()) {
				throw new OperationFailureException("Configuration "+config.getId()+" is locked");
			}
		}
		config.setLastUpdated(new Date());
		DigestConfiguration saved = this.confRepo.save(config);
		return new OpAck<>(AckStatus.SUCCESS, "Configuration Successfully Saved", saved,"config-save");
	}

	//  Download Configuration Owned or Published
	@RequestMapping(value="/{id}/download", method=RequestMethod.GET)
	public void download(@PathVariable("id") String id, final HttpServletResponse rsp) throws NotFoundException, IOException {
		Account a = this.accountService.getCurrentUser();
		DigestConfiguration conf = confRepo.findOne(id);
		if(conf == null || !(conf.getOwner().equals(a.getUsername()) || conf.isPublished())){
			throw new NotFoundException("Configuration "+id+" Not Found");
		}
		else {
			rsp.setContentType("application/json");
			rsp.setHeader("Content-disposition", "attachment;filename=configuration.json");
			rsp.getOutputStream().write(mapper.writeValueAsBytes(conf.getPayload()));
		}
	}

	//  Lock Configuration Owned
	@RequestMapping(value="/{id}/lock/{value}", method=RequestMethod.POST)
	@ResponseBody
	public OpAck<DigestConfiguration> lock(@PathVariable("id") String id, @PathVariable("value") boolean value) throws NotFoundException {
		Account a = this.accountService.getCurrentUser();
		DigestConfiguration configuration = confRepo.findByOwnerAndId(a.getUsername(), id);
		if(configuration != null){
			configuration.setLocked(value);
			DigestConfiguration saved = this.confRepo.save(configuration);
			saved.setViewOnly(!a.getUsername().equals(saved.getOwner()) || saved.isLocked());
			return new OpAck<>(AckStatus.SUCCESS, "Configuration Successfully " + (value ? "Locked" : "Unlocked") , saved,"config-lock");
		}
		else {
			throw new NotFoundException("Configuration "+id+" Not Found");
		}
	}

	//  Publish Configuration Owned
	@RequestMapping(value="/{id}/publish", method=RequestMethod.POST)
	@ResponseBody
	public OpAck<DigestConfiguration> publish(@PathVariable("id") String id) throws NotFoundException {
		Account a = this.accountService.getCurrentUser();
		DigestConfiguration configuration = confRepo.findByOwnerAndId(a.getUsername(), id);
		if(configuration != null){
			configuration.setPublished(true);
			DigestConfiguration saved = this.confRepo.save(configuration);
			saved.setViewOnly(!a.getUsername().equals(saved.getOwner()) || saved.isLocked());
			return new OpAck<>(AckStatus.SUCCESS, "Configuration Successfully Published", saved,"config-publish");
		}
		else {
			throw new NotFoundException("Configuration "+id+" Not Found");
		}
	}
}