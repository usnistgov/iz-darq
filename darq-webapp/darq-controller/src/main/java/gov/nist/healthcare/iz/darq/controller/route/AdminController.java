package gov.nist.healthcare.iz.darq.controller.route;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import gov.nist.healthcare.iz.darq.access.service.EmailService;
import gov.nist.healthcare.iz.darq.controller.domain.FreeMarkerEvalRequest;
import gov.nist.healthcare.iz.darq.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;


@RestController
@RequestMapping("/api/admin")
public class EmailTemplateController {

	@Autowired
	private EmailService emailService;

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

	@RequestMapping(value = "/email/freemarker", method = RequestMethod.POST)
	@ResponseBody
	public String evaluateBody(@RequestBody FreeMarkerEvalRequest request) throws IOException, TemplateException {
		Template template = new Template("name", new StringReader(request.getBody()), new Configuration());
		return FreeMarkerTemplateUtils.processTemplateIntoString(template, request.getParams());
	}

}