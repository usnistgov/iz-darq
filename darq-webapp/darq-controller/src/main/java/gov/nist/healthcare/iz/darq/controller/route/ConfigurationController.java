package gov.nist.healthcare.iz.darq.controller.route;


import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import gov.nist.healthcare.iz.darq.access.security.CustomSecurityExpressionRoot;
import gov.nist.healthcare.iz.darq.configuration.exception.InvalidConfigurationPayload;
import gov.nist.healthcare.iz.darq.controller.service.DescriptorService;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import gov.nist.healthcare.iz.darq.users.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	private ConfigurationService configService;
	@Autowired
	private DigestConfigurationRepository confRepo;
	@Autowired
	private ObjectMapper mapper;
	@Autowired
	private DescriptorService descriptorService;

	//  Get All Accessible Configuration (Descriptor)
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
	@PreAuthorize("AccessOperation(CONFIGURATION, VIEW, GLOBAL, PUBLIC())")
	public List<ConfigurationDescriptor> all(
			@AuthenticationPrincipal User user) {
    	return confRepo.findAccessibleTo(user.getId()).stream()
    	.map(x -> this.descriptorService.getConfigurationDescriptor(x))
    	.collect(Collectors.toList());
    }

	//  Get Configuration by Id (Owned or Published [viewOnly])
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	@ResponseBody
	@PreAuthorize("AccessResource(#request, CONFIGURATION, VIEW, #id)")
	public DigestConfiguration get(
			HttpServletRequest request,
			@PathVariable("id") String id) {
    	return (DigestConfiguration) request.getAttribute(CustomSecurityExpressionRoot.RESOURCE_ATTRIBUTE);
	}

	// Get All Accessible Configurations
	@RequestMapping(value = "/{id}/descriptor", method = RequestMethod.GET)
	@ResponseBody
	@PreAuthorize("AccessResource(#request, CONFIGURATION, VIEW, #id)")
	public ConfigurationDescriptor descriptor(
			HttpServletRequest request,
			@PathVariable("id") String id) {
		return this.descriptorService.getConfigurationDescriptor((DigestConfiguration) request.getAttribute(CustomSecurityExpressionRoot.RESOURCE_ATTRIBUTE));
	}

	//  Clone Configuration by Id (Owned or Published)
	@RequestMapping(value="/{id}/clone", method=RequestMethod.POST)
	@ResponseBody
	@PreAuthorize("AccessResource(#request, CONFIGURATION, CLONE, #id)")
	public OpAck<DigestConfiguration> clone(
			HttpServletRequest request,
			@AuthenticationPrincipal User user,
			@PathVariable("id") String id) {
		DigestConfiguration conf = (DigestConfiguration) request.getAttribute(CustomSecurityExpressionRoot.RESOURCE_ATTRIBUTE);
		conf.setId(null);
		conf.setName("[Clone] "+conf.getName());
		conf.setOwner(user.getUsername());
		conf.setOwnerId(user.getId());
		conf.setPublished(false);
		conf.setLocked(false);
		DigestConfiguration saved = this.confRepo.save(conf);
		return new OpAck<>(AckStatus.SUCCESS, "Configuration Successfully Cloned", saved, "config-clone");
	}


	//  Delete Configuration by Id (Owned)
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	@ResponseBody
	@PreAuthorize("AccessResource(#request, CONFIGURATION, DELETE, #id)")
	public OpAck<DigestConfiguration> delete(
			HttpServletRequest request,
			@PathVariable("id") String id) {
		DigestConfiguration x = (DigestConfiguration) request.getAttribute(CustomSecurityExpressionRoot.RESOURCE_ATTRIBUTE);
		this.confRepo.delete(x);
		return new OpAck<>(AckStatus.SUCCESS, "Configuration Successfully Deleted", null,"config-delete");
	}

	//  Save Configuration (Owned and not Locked or New)
	// CREATE / SAVE
	@RequestMapping(value="/", method=RequestMethod.POST)
	@ResponseBody
	@PreAuthorize(
			"#config.id != null ? " +
			"AccessResource(#request, CONFIGURATION, EDIT, #config.id) : " +
			"AccessOperation(CONFIGURATION, CREATE, GLOBAL)"
	)
	public OpAck<DigestConfiguration> create(
			HttpServletRequest request,
			@AuthenticationPrincipal User user,
			@RequestBody DigestConfiguration config) throws OperationFailureException {
		if(config.getId() == null || config.getId().isEmpty()){
			config.setOwner(user.getUsername());
			config.setOwnerId(user.getId());
			config.setPublished(false);
			config.setLocked(false);
		} else {
			DigestConfiguration existing = (DigestConfiguration) request.getAttribute(CustomSecurityExpressionRoot.RESOURCE_ATTRIBUTE);
			if(existing.isLocked()) {
				throw new OperationFailureException("Configuration "+config.getId()+" is locked");
			}
			// Non Overridable fields
			config.setOwnerId(existing.getOwnerId());
			config.setOwner(existing.getOwner());
			config.setPublished(existing.isPublished());
			config.setLocked(existing.isLocked());
		}
		config.setLastUpdated(new Date());
		try {
			this.configService.validateConfigurationPayload(config.getPayload(), true);
			this.configService.validateAgeGroups(config.getPayload().getAgeGroups(), false);
		} catch (InvalidConfigurationPayload invalidConfigurationPayload) {
			throw new OperationFailureException(invalidConfigurationPayload.getMessage());
		}
		DigestConfiguration saved = this.confRepo.save(config);
		return new OpAck<>(AckStatus.SUCCESS, "Configuration Successfully Saved", saved,"config-save");
	}

	//  Download Configuration Owned or Published
	@RequestMapping(value="/{id}/download", method=RequestMethod.GET)
	@PreAuthorize("AccessResource(#request, CONFIGURATION, VIEW, #id)")
	public void download(
			HttpServletRequest request,
			@PathVariable("id") String id,
			final HttpServletResponse rsp) throws IOException {
		DigestConfiguration conf = (DigestConfiguration) request.getAttribute(CustomSecurityExpressionRoot.RESOURCE_ATTRIBUTE);
		rsp.setContentType("application/json");
		rsp.setHeader("Content-disposition", "attachment;filename=configuration.json");
		rsp.getOutputStream().write(mapper.writeValueAsBytes(conf.getPayload()));
	}

	//  Lock Configuration Owned
	@RequestMapping(value="/{id}/lock/{value}", method=RequestMethod.POST)
	@ResponseBody
	@PreAuthorize("AccessResource(#request, CONFIGURATION, EDIT, #id)")
	public OpAck<DigestConfiguration> lock(
			HttpServletRequest request,
			@PathVariable("id") String id,
			@PathVariable("value") boolean value) {
		DigestConfiguration configuration = (DigestConfiguration) request.getAttribute(CustomSecurityExpressionRoot.RESOURCE_ATTRIBUTE);
		configuration.setLocked(value);
		DigestConfiguration saved = this.confRepo.save(configuration);
		return new OpAck<>(AckStatus.SUCCESS, "Configuration Successfully " + (value ? "Locked" : "Unlocked") , saved,"config-lock");
	}

	//  Publish Configuration Owned
	@RequestMapping(value="/{id}/publish", method=RequestMethod.POST)
	@ResponseBody
	@PreAuthorize("AccessResource(#request, CONFIGURATION, PUBLISH, #id)")
	public OpAck<DigestConfiguration> publish(
			HttpServletRequest request,
			@PathVariable("id") String id) {
		DigestConfiguration configuration = (DigestConfiguration) request.getAttribute(CustomSecurityExpressionRoot.RESOURCE_ATTRIBUTE);
		configuration.setPublished(true);
		DigestConfiguration saved = this.confRepo.save(configuration);
		return new OpAck<>(AckStatus.SUCCESS, "Configuration Successfully Published", saved,"config-publish");
	}
}