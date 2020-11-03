package gov.nist.healthcare.iz.darq.controller.route;


import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.access.service.EmailService;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationKeyValue;
import gov.nist.healthcare.iz.darq.model.*;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import gov.nist.healthcare.iz.darq.service.exception.PropertyException;
import gov.nist.healthcare.iz.darq.service.impl.ToolConfigurationService;
import gov.nist.healthcare.iz.darq.service.impl.WebContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/api/admin")
public class AdminController {

	@Autowired
	private EmailService emailService;
	@Autowired
	private WebContentService webContentService;
	@Autowired
	private ToolConfigurationService toolConfigurationService;

	@RequestMapping(value = "/email/templates", method = RequestMethod.GET)
	@ResponseBody
	public List<EmailTemplate> templates() {
		return this.emailService.findAll();
	}

	@RequestMapping(value = "/email/templates", method = RequestMethod.POST)
	@ResponseBody
	public List<EmailTemplate> setTemplates(@RequestBody List<EmailTemplate> templates) {
		for(EmailTemplate template: templates) {
			this.emailService.setEmailTemplate(template);
		}
		return this.emailService.findAll();
	}

	@RequestMapping(value = "/web-content", method = RequestMethod.GET)
	@ResponseBody
	public WebContent getWebContent() {
		return this.webContentService.getWebContent();
	}

	@RequestMapping(value = "/web-content", method = RequestMethod.POST)
	@ResponseBody
	public WebContent saveWebContent(@RequestBody WebContent webContent) {
		return this.webContentService.save(webContent);
	}

	@RequestMapping(value = "/tool-configuration", method = RequestMethod.GET)
	@ResponseBody
	public ToolConfiguration getToolConfiguration() {
		return this.toolConfigurationService.getToolConfiguration();
	}

	@RequestMapping(value = "/tool-configuration", method = RequestMethod.POST)
	@ResponseBody
	public OpAck<Set<OpAck<Void>>> saveToolConfigKeys(@RequestBody Set<ToolConfigurationKeyValue> properties) throws OperationFailureException {
		try {
			Set<OpAck<Void>> status = this.toolConfigurationService.updateProperties(properties);
			return new OpAck<>(OpAck.AckStatus.SUCCESS, "Configuration Updated Successfully", status, "UPDATE_TOOL_CONFIG");
		} catch (PropertyException e) {
			throw new OperationFailureException(e.getMessage());
		}
	}

	@RequestMapping(value = "/tool-configuration/status", method = RequestMethod.GET)
	@ResponseBody
	public Set<OpAck<Void>> checkConfigurationStatus() {
		return this.toolConfigurationService.checkConfigurationStatus();
	}
}