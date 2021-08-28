package gov.nist.healthcare.iz.darq.controller.route;

import java.util.ArrayList;
import java.util.List;

import gov.nist.healthcare.iz.darq.access.security.CustomSecurityExpressionRoot;
import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportTemplate;
import gov.nist.healthcare.iz.darq.controller.domain.ReportTemplateCreate;
import gov.nist.healthcare.iz.darq.controller.service.DescriptorService;
import gov.nist.healthcare.iz.darq.model.UserUploadedFile;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import gov.nist.healthcare.iz.darq.service.impl.ADFStorage;
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

import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.domain.OpAck.AckStatus;
import gov.nist.healthcare.iz.darq.model.TemplateDescriptor;
import gov.nist.healthcare.iz.darq.model.DigestConfiguration;
import gov.nist.healthcare.iz.darq.repository.ADFMetaDataRepository;
import gov.nist.healthcare.iz.darq.repository.DigestConfigurationRepository;
import gov.nist.healthcare.iz.darq.repository.TemplateRepository;
import gov.nist.healthcare.iz.darq.service.utils.ConfigurationService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/template")
public class TemplateController {

	@Autowired
	private ConfigurationService configService;
	@Autowired
	private TemplateRepository templateRepo;
	@Autowired
	private DigestConfigurationRepository confRepo;
	@Autowired
	private ADFMetaDataRepository repo;
	@Autowired
	private ADFStorage adfStorage;
	@Autowired
	private DescriptorService descriptorService;

	// Get All Accessible Report Templates
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
	@PreAuthorize("AccessOperation(REPORT_TEMPLATE, VIEW, GLOBAL, PUBLIC())")
	public List<TemplateDescriptor> all(
			@AuthenticationPrincipal User user) {
		List<ReportTemplate> templates = templateRepo.findAccessibleTo(user.getId());
		List<TemplateDescriptor> result = new ArrayList<>();
		List<DigestConfiguration> configurations = this.confRepo.findAccessibleTo(user.getId());
		for(ReportTemplate rt : templates){
			result.add(this.descriptorService.getTemplateDescriptor(rt, this.configService.compatibilities(rt.getConfiguration(), configurations)));
		}
		return result;
    }

	// Get All Accessible Report Templates
	@RequestMapping(value = "/{id}/descriptor", method = RequestMethod.GET)
	@ResponseBody
	@PreAuthorize("AccessResource(#request, REPORT_TEMPLATE, VIEW, #id)")
	public TemplateDescriptor descriptor(
			HttpServletRequest request,
			@AuthenticationPrincipal User user,
			@PathVariable("id") String id) {
		ReportTemplate rt = (ReportTemplate) request.getAttribute(CustomSecurityExpressionRoot.RESOURCE_ATTRIBUTE);
		List<DigestConfiguration> configurations = this.confRepo.findAccessibleTo(user.getId());
		return this.descriptorService.getTemplateDescriptor(rt, this.configService.compatibilities(rt.getConfiguration(), configurations));
	}

	//  Save Report Template (Owned and not Locked or New)
	@RequestMapping(value="/", method=RequestMethod.POST)
	@ResponseBody
	@PreAuthorize(
			"#template.id != null ? " +
			"AccessResource(#request, REPORT_TEMPLATE, EDIT, #template.id) : " +
			"AccessOperation(REPORT_TEMPLATE, CREATE, GLOBAL)"
	)
	public OpAck<ReportTemplate> save(
			HttpServletRequest request,
			@AuthenticationPrincipal User user,
			@RequestBody ReportTemplate template) {
		if(template.getId() == null || template.getId().isEmpty()) {
			template.setId(null);
			template.setOwner(user.getUsername());
			template.setOwnerId(user.getId());
		}
		ReportTemplate saved = this.templateRepo.save(template);
		return new OpAck<>(AckStatus.SUCCESS, "Report Template Successfully Saved", saved, "report-template-save");
	}

	//  Create Report Template With Configuration
	@RequestMapping(value="/create", method=RequestMethod.POST)
	@ResponseBody
	@PreAuthorize("AccessOperation(REPORT_TEMPLATE, CREATE, GLOBAL)")
	public OpAck<ReportTemplate> create(
			@AuthenticationPrincipal User user,
			@RequestBody ReportTemplateCreate create) throws OperationFailureException {
		ReportTemplate template = new ReportTemplate();
		template.setId(null);
		template.setOwner(user.getUsername());
		template.setOwnerId(user.getId());
		template.setName(create.getName());

		DigestConfiguration configuration = this.confRepo.findByOwnerIdOrReadOnly(create.getConfigurationId(), user.getId());
		if(configuration == null){
			throw new OperationFailureException("Can't use configuration "+create.getConfigurationId());
		} else {
			template.setConfiguration(configuration.getPayload());
			ReportTemplate saved = this.templateRepo.save(template);
			return new OpAck<>(AckStatus.SUCCESS, "Report Template Successfully Saved", saved, "report-template-save");
		}
	}

	//  Get Report Template by Id (Owned or Published [viewOnly])
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	@ResponseBody
	@PreAuthorize("AccessResource(#request, REPORT_TEMPLATE, VIEW, #id)")
	public ReportTemplate get(
			HttpServletRequest request,
			@PathVariable("id") String id) {
		return (ReportTemplate) request.getAttribute(CustomSecurityExpressionRoot.RESOURCE_ATTRIBUTE);
	}

	//  Clone Report Template by Id (Owned or Published)
	@RequestMapping(value="/{id}/clone", method=RequestMethod.POST)
	@ResponseBody
	@PreAuthorize("AccessResource(#request, REPORT_TEMPLATE, CLONE, #id)")
	public OpAck<ReportTemplate> clone(
			HttpServletRequest request,
			@AuthenticationPrincipal User user,
			@PathVariable("id") String id) {
		ReportTemplate template =  (ReportTemplate) request.getAttribute(CustomSecurityExpressionRoot.RESOURCE_ATTRIBUTE);
		template.setId(null);
		template.setName("[Clone] "+template.getName());
		template.setOwner(user.getUsername());
		template.setOwnerId(user.getId());
		template.setPublished(false);
		ReportTemplate saved = this.templateRepo.save(template);
		return new OpAck<>(AckStatus.SUCCESS, "Report Template Successfully Cloned", saved, "report-template-clone");
	}

	//  Delete Report Template by Id (Owned)
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	@ResponseBody
	@PreAuthorize("AccessResource(#request, REPORT_TEMPLATE, DELETE, #id)")
	public OpAck<ReportTemplate> delete(
			HttpServletRequest request,
			@PathVariable("id") String id) {
		ReportTemplate x =  (ReportTemplate) request.getAttribute(CustomSecurityExpressionRoot.RESOURCE_ATTRIBUTE);
		this.templateRepo.delete(x);
		return new OpAck<>(AckStatus.SUCCESS, "Report Template Successfully Deleted", null,"report-template-delete");
	}

	//  Publish Report Template Owned
	@RequestMapping(value="/{id}/publish", method=RequestMethod.POST)
	@ResponseBody
	@PreAuthorize("AccessResource(#request, REPORT_TEMPLATE, PUBLISH, #id)")
	public OpAck<ReportTemplate> publish(
			HttpServletRequest request,
			@PathVariable("id") String id) {
		ReportTemplate template =  (ReportTemplate) request.getAttribute(CustomSecurityExpressionRoot.RESOURCE_ATTRIBUTE);
		template.setPublished(true);
		ReportTemplate saved = this.templateRepo.save(template);
		return new OpAck<>(AckStatus.SUCCESS, "Report Template Successfully Published", saved,"report-template-publish");
	}

	// Get Accessible Templates Compatible with ADF by Id
	@RequestMapping(value="/for/{id}", method=RequestMethod.GET)
	@ResponseBody
	@PreAuthorize("AccessResource(#request, ADF, VIEW, #id)")
	public List<TemplateDescriptor> template(
			@AuthenticationPrincipal User user,
			HttpServletRequest request,
			@PathVariable("id") String id) {
		UserUploadedFile md =  (UserUploadedFile) request.getAttribute(CustomSecurityExpressionRoot.RESOURCE_ATTRIBUTE);
		List<TemplateDescriptor> result = new ArrayList<>();
		if(md != null){
			List<ReportTemplate> templates = this.templateRepo.findAccessibleTo(user.getId());
			for(ReportTemplate template : templates){
				if(this.configService.compatible(template.getConfiguration(), md.getConfiguration())){
					result.add(this.descriptorService.getTemplateDescriptor(template, this.configService.compatibilities(template.getConfiguration(),null)));
				}
			}
		}
		return result;
	}
}