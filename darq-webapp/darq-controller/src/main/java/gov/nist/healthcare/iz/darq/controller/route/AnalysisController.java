package gov.nist.healthcare.iz.darq.controller.route;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.service.AccountService;
import gov.nist.healthcare.iz.darq.adf.service.ADFStore;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisReport;
import gov.nist.healthcare.iz.darq.analyzer.domain.ReportTemplate;
import gov.nist.healthcare.iz.darq.analyzer.service.ReportService;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;
import gov.nist.healthcare.iz.darq.repository.TemplateRepository;

@RestController
@RequestMapping("/api")
public class AnalysisController {

	@Autowired
	private AccountService accountService;
	@Autowired
	private TemplateRepository templateRepo;
	@Autowired
	private ReportService report;
	@Autowired
	private ADFStore storage;
	
    @RequestMapping(value = "/analyze/{tId}/{fId}", method = RequestMethod.GET)
    @ResponseBody
    public AnalysisReport analyze(@PathVariable("tId") String tId, @PathVariable("fId") String fId) throws Exception {
    	Account a = this.accountService.getCurrentUser();
    	ADFile file = this.storage.getFile(fId, a.getUsername());
    	ReportTemplate template = this.templateRepo.findByIdAndOwner(tId, a.getUsername());
    	if(file != null && template != null){
    		AnalysisReport report =  this.report.analyse(file, template);
    		report.setAdfName(this.storage.get(fId, a.getUsername()).getName());
    		return report;
    	}
    	return null;
    }
    
}