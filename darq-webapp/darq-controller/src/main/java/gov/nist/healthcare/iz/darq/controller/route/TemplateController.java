package gov.nist.healthcare.iz.darq.controller.route;

import java.util.ArrayList;
import java.util.List;

import gov.nist.healthcare.iz.darq.controller.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.controller.exception.OperationFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.service.AccountService;
import gov.nist.healthcare.iz.darq.analyzer.domain.ReportTemplate;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.domain.OpAck.AckStatus;
import gov.nist.healthcare.iz.darq.controller.domain.TemplateDescriptor;
import gov.nist.healthcare.iz.darq.digest.domain.ADFMetaData;
import gov.nist.healthcare.iz.darq.model.DigestConfiguration;
import gov.nist.healthcare.iz.darq.repository.ADFMetaDataRepository;
import gov.nist.healthcare.iz.darq.repository.DigestConfigurationRepository;
import gov.nist.healthcare.iz.darq.repository.TemplateRepository;
import gov.nist.healthcare.iz.darq.service.utils.CodeSetService;
import gov.nist.healthcare.iz.darq.service.utils.ConfigurationService;

@RestController
@RequestMapping("/api/template")
public class TemplateController {

	@Autowired
	private AccountService accountService;
	@Autowired
	private ConfigurationService configService;
	@Autowired
	private TemplateRepository templateRepo;
	@Autowired
	private DigestConfigurationRepository confRepo;
	@Autowired
	private ADFMetaDataRepository repo;
	@Autowired
	private CodeSetService codeSet;

	// Get All Accessible Report Templates
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public List<TemplateDescriptor> all() {
		Account a = this.accountService.getCurrentUser();
		List<ReportTemplate> templates = templateRepo.findAccessible(a.getUsername());
		List<TemplateDescriptor> result = new ArrayList<>();
		List<DigestConfiguration> configurations = this.confRepo.findAccessible(a.getUsername());
		for(ReportTemplate rt : templates){
			result.add(
					new TemplateDescriptor(
							rt.getId(),
							rt.getName(),
							rt.getOwner(),
							this.configService.compatibilities(rt.getConfiguration(), configurations, a.getUsername()),
							!a.getUsername().equals(rt.getOwner())
					)
			);
		}
		return result;
    }

	//  Save Report Template (Owned and not Locked or New)
	@RequestMapping(value="/", method=RequestMethod.POST)
	@ResponseBody
	public OpAck<ReportTemplate> create(@RequestBody ReportTemplate template) throws OperationFailureException {
		Account a = this.accountService.getCurrentUser();
		if(template.getId() == null || template.getId().isEmpty()){
			template.setId(null);
			template.setOwner(a.getUsername());
		} else {
			ReportTemplate existing = this.templateRepo.findByIdAndOwner(template.getId(), a.getUsername());
			if(existing == null) {
				throw new OperationFailureException("Can't save report template "+template.getId());
			}
		}
		ReportTemplate saved = this.templateRepo.save(template);
		return new OpAck<>(AckStatus.SUCCESS, "Report Template Successfully Saved", saved, "report-template-save");
	}

	//  Get Report Template by Id (Owned or Published [viewOnly])
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	@ResponseBody
	public ReportTemplate get(@PathVariable("id") String id) throws NotFoundException {
		Account a = this.accountService.getCurrentUser();
		ReportTemplate template = templateRepo.findOne(id);
		if(template == null || (!(template.getOwner().equals(a.getUsername()) && !template.isPublished()))){
			throw new NotFoundException("Report Template "+id+" Not Found");
		}
		else {
			template.setViewOnly(!a.getUsername().equals(template.getOwner()));
			return template;
		}
	}

	//  Clone Report Template by Id (Owned or Published)
	@RequestMapping(value="/{id}/clone", method=RequestMethod.GET)
	@ResponseBody
	public OpAck<ReportTemplate> clone(@PathVariable("id") String id) throws NotFoundException {
		Account a = this.accountService.getCurrentUser();
		ReportTemplate template = templateRepo.findOne(id);
		if(template == null || (!(template.getOwner().equals(a.getUsername()) && !template.isPublished()))){
			throw new NotFoundException("Report Template "+id+" Not Found");
		}
		else {
			template.setId(null);
			template.setName("[Clone] "+template.getName());
			template.setOwner(a.getUsername());
			template.setPublished(false);
			ReportTemplate saved = this.templateRepo.save(template);
			return new OpAck<>(AckStatus.SUCCESS, "Report Template Successfully Cloned", saved, "report-template-clone");
		}
	}

	//  Delete Report Template by Id (Owned)
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	@ResponseBody
	public OpAck<ReportTemplate> delete(@PathVariable("id") String id) throws NotFoundException {
		Account a = this.accountService.getCurrentUser();
		ReportTemplate x = templateRepo.findByIdAndOwner(id, a.getUsername());
		if(x != null){
			this.templateRepo.delete(x);
			return new OpAck<>(AckStatus.SUCCESS, "Report Template Successfully Deleted", null,"report-template-delete");
		}
		else {
			throw new NotFoundException("Report Template "+id+" Not Found");
		}
	}

	//  Publish Report Template Owned
	@RequestMapping(value="/{id}/publish", method=RequestMethod.GET)
	@ResponseBody
	public OpAck<ReportTemplate> publish(@PathVariable("id") String id) throws NotFoundException {
		Account a = this.accountService.getCurrentUser();
		ReportTemplate template = this.templateRepo.findByIdAndOwner(id, a.getUsername());
		if(template != null){
			template.setPublished(true);
			ReportTemplate saved = this.templateRepo.save(template);
			return new OpAck<>(AckStatus.SUCCESS, "Report Template Successfully Published", saved,"report-template-publish");
		}
		else {
			throw new NotFoundException("Report Template "+id+" Not Found");
		}
	}

	// Get Code Set
	@RequestMapping(value="/codesets/{id}", method=RequestMethod.GET)
	@ResponseBody
	public List<String> codeset(@PathVariable("id") String id) throws IllegalAccessException {
		if(id.equals("patient")){
			return codeSet.patientCodes();
		}
		else {
			return codeSet.vaccinationCodes();
		}
	}


	// Get Accessible Templates Compatible with ADF by Id
	@RequestMapping(value="/for/{id}", method=RequestMethod.GET)
	@ResponseBody
	public List<TemplateDescriptor> template(@PathVariable("id") String id) {
		Account a = this.accountService.getCurrentUser();
		ADFMetaData md = repo.findByIdAndOwner(id, a.getUsername());
		List<TemplateDescriptor> result = new ArrayList<>();
		if(md != null){
			List<ReportTemplate> templates = this.templateRepo.findAccessible(a.getUsername());
			for(ReportTemplate template : templates){
				if(this.configService.compatible(md.getConfiguration(), template.getConfiguration())){
					result.add(
							new TemplateDescriptor(
									template.getId(),
									template.getName(),
									template.getName(),
									null,
									!a.getUsername().equals(template.getOwner())
							)
					);
				}
			}
		}
		return result;
	}
	

}