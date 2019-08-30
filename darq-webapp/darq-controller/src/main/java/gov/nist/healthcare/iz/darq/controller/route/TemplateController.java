package gov.nist.healthcare.iz.darq.controller.route;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import gov.nist.healthcare.iz.darq.controller.domain.OpAck;
import gov.nist.healthcare.iz.darq.controller.domain.OpAck.AckStatus;
import gov.nist.healthcare.iz.darq.controller.domain.TemplateDescriptor;
import gov.nist.healthcare.iz.darq.digest.domain.ADFMetaData;
import gov.nist.healthcare.iz.darq.model.DigestConfiguration;
import gov.nist.healthcare.iz.darq.repository.ADFMetaDataRepository;
import gov.nist.healthcare.iz.darq.repository.DigestConfigurationRepository;
import gov.nist.healthcare.iz.darq.repository.TemplateRepository;
import gov.nist.healthcare.iz.darq.service.utils.CodeSetService;
import gov.nist.healthcare.iz.darq.service.utils.ConfigurationService;

@RestController
@RequestMapping("/api")
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
	
    @RequestMapping(value = "/template", method = RequestMethod.GET)
    @ResponseBody
    public List<TemplateDescriptor> all() {
		Account a = this.accountService.getCurrentUser();
		List<ReportTemplate> templates = templateRepo.findByOwner(a.getUsername());
		List<TemplateDescriptor> result = new ArrayList<>();
		List<DigestConfiguration> configurations = this.confRepo.findAccessible(a.getUsername());
		for(ReportTemplate rt : templates){
			result.add(new TemplateDescriptor(rt.getId(), rt.getName(), rt.getOwner(), this.configService.compatibilities(rt.getConfiguration(), configurations)));
		}
		return result;
    }
    
	@RequestMapping(value="/template", method=RequestMethod.POST)
	@ResponseBody
	public OpAck create(@RequestBody ReportTemplate template, final HttpServletRequest request) throws Exception{
		Account a = this.accountService.getCurrentUser();
		if(template.getId() == null || template.getId().isEmpty()){
			template.setId(null);
			template.setOwner(a.getUsername());
		}
		this.templateRepo.save(template);
		return new OpAck(AckStatus.SUCCESS, template.getId(), "upload");
	}
	
	@RequestMapping(value="/template/codesets/{id}", method=RequestMethod.GET)
	@ResponseBody
	public List<String> codeset(@PathVariable("id") String id, final HttpServletResponse rsp) throws Exception {
		if(id.equals("patient")){
			return codeSet.patientCodes();
		}
		else {
			return codeSet.vaccinationCodes();
		}
	}
	
	@RequestMapping(value="/template/{id}", method=RequestMethod.GET)
	@ResponseBody
	public ReportTemplate get(@PathVariable("id") String id, final HttpServletResponse rsp) throws Exception {
		Account a = this.accountService.getCurrentUser();
		ReportTemplate template = templateRepo.findOne(id);
		if(template == null || !(template.getOwner().equals(a.getUsername()) || template.isPublished())){
			rsp.sendError(404, "Template "+id+" Not Found");
			return null;
		}
		else {
			template.setVonly(!a.getUsername().equals(template.getOwner()));
			return template;
		}
	}
	
	@RequestMapping(value="/template/{id}", method=RequestMethod.DELETE)
	@ResponseBody
	public OpAck delete(@PathVariable("id") String id, final HttpServletRequest request) throws Exception {
		Account a = this.accountService.getCurrentUser();
		ReportTemplate x = templateRepo.findByIdAndOwner(id, a.getUsername());
		if(x != null){
			this.templateRepo.delete(x);
			return new OpAck(AckStatus.SUCCESS, "", "config-delete");
		}
		else {
			return new OpAck(AckStatus.FAILURE, "Not Found", "config-delete");
		}
	}
	
	@RequestMapping(value="/template/for/{id}", method=RequestMethod.GET)
	@ResponseBody
	public List<TemplateDescriptor> template(@PathVariable("id") String id, final HttpServletRequest request) throws Exception {
		Account a = this.accountService.getCurrentUser();
		ADFMetaData md = repo.findByIdAndOwner(id, a.getUsername());
		List<TemplateDescriptor> result = new ArrayList<>();
		if(md != null){
			List<ReportTemplate> templates = this.templateRepo.findAccessible(a.getUsername());
			for(ReportTemplate template : templates){
				if(this.configService.compatible(md.getConfiguration(), template.getConfiguration())){
					result.add(new TemplateDescriptor(template.getId(), template.getName(), template.getName(), null));
				}
			}
		}
		return result;
	}
	

}