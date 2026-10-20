package gov.nist.healthcare.iz.darq.controller.route;


import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.domain.OpAck.AckStatus;
import gov.nist.healthcare.iz.darq.access.security.CustomSecurityExpressionRoot;
import gov.nist.healthcare.iz.darq.configuration.exception.InvalidConfigurationPayload;
import gov.nist.healthcare.iz.darq.controller.service.DescriptorService;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.model.ConfigurationDescriptor;
import gov.nist.healthcare.iz.darq.model.DigestConfiguration;
import gov.nist.healthcare.iz.darq.repository.DigestConfigurationRepository;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import gov.nist.healthcare.iz.darq.service.impl.SimpleConfigurationService;
import gov.nist.healthcare.iz.darq.service.utils.ConfigurationService;
import gov.nist.healthcare.iz.darq.users.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
	@Autowired
	private SimpleConfigurationService simpleConfigurationService;

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

	//  Get Accessible Configurations compatible with one configuration (Descriptor)
    @RequestMapping(value="/{id}/compatible", method = RequestMethod.GET)
    @ResponseBody
	@PreAuthorize("AccessOperation(CONFIGURATION, VIEW, GLOBAL, PUBLIC()) && AccessResource(CONFIGURATION, VIEW, #id)")
	public List<ConfigurationDescriptor> allCompatible(
			@AuthenticationPrincipal User user,
			@PathVariable("id") String id) {
		List<DigestConfiguration> accessibleConfigurations = confRepo.findAccessibleTo(user.getId());
		DigestConfiguration sourceConfiguration = accessibleConfigurations.stream()
				.filter(digestConfiguration -> id.equals(digestConfiguration.getId()))
				.findFirst()
				.get();
    	return accessibleConfigurations.stream()
		.filter(digestConfiguration -> simpleConfigurationService.compatible(sourceConfiguration.getPayload(), digestConfiguration.getPayload()))
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

	//  Import a Configuration Payload as a New Configuration
	@RequestMapping(value="/import", method=RequestMethod.POST)
	@ResponseBody
	@PreAuthorize("AccessOperation(CONFIGURATION, CREATE, GLOBAL)")
	public OpAck<DigestConfiguration> importConfiguration(
			@AuthenticationPrincipal User user,
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "description", required = false) String description) throws OperationFailureException, IOException {
		ConfigurationPayload payload;
		try {
			payload = mapper.readValue(file.getInputStream(), ConfigurationPayload.class);
		} catch (IOException e) {
			throw new OperationFailureException("Could not read configuration file : " + e.getMessage());
		}
		if(payload == null) {
			throw new OperationFailureException("Configuration file is empty");
		}
		try {
			this.configService.validateConfigurationPayload(payload, true);
			this.configService.validateAgeGroups(payload.getAgeGroups(), false);
		} catch (InvalidConfigurationPayload invalidConfigurationPayload) {
			throw new OperationFailureException(invalidConfigurationPayload.getMessage());
		}

		DigestConfiguration config = new DigestConfiguration();
		config.setPayload(payload);
		config.setName(name != null && !name.trim().isEmpty() ? name.trim() : defaultImportName(file.getOriginalFilename()));
		config.setDescription(description);
		config.setOwner(user.getUsername());
		config.setOwnerId(user.getId());
		config.setPublished(false);
		config.setLocked(false);
		config.setLastUpdated(new Date());

		DigestConfiguration saved = this.confRepo.save(config);
		return new OpAck<>(AckStatus.SUCCESS, "Configuration Successfully Imported", saved, "config-import");
	}

	private String defaultImportName(String fileName) {
		if(fileName == null || fileName.trim().isEmpty()) {
			return "[Imported] Configuration";
		}
		String base = fileName.substring(fileName.lastIndexOf('/') + 1);
		int dot = base.lastIndexOf('.');
		if(dot > 0) {
			base = base.substring(0, dot);
		}
		return "[Imported] " + base;
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