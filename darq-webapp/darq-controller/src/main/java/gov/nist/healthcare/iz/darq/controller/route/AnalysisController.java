package gov.nist.healthcare.iz.darq.controller.route;


import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.DataTable;
import gov.nist.healthcare.iz.darq.analyzer.model.template.DataViewQuery;
import gov.nist.healthcare.iz.darq.controller.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import gov.nist.healthcare.auth.service.AccountService;
import gov.nist.healthcare.iz.darq.adf.service.ADFStore;
import gov.nist.healthcare.iz.darq.analyzer.service.ReportService;
import gov.nist.healthcare.iz.darq.repository.TemplateRepository;

@RestController
@RequestMapping("/api/analyze")
public class AnalysisController {

	@Autowired
	private AccountService accountService;
	@Autowired
	private TemplateRepository templateRepo;
	@Autowired
	private ReportService report;
	@Autowired
	private ADFStore storage;


	@RequestMapping(value = "/query/{fId}", method = RequestMethod.POST)
	@ResponseBody
	public DataTable analyze(@RequestBody DataViewQuery query, @PathVariable("fId") String fId) throws Exception {
    	Account a = this.accountService.getCurrentUser();
    	ADFile file = this.storage.getFile(fId, a.getUsername());
    	if(file != null){
			return this.report.singleQuery(file, query);
    	} else {
    		throw new NotFoundException(" ADF File "+fId+" Not Found");
		}
	}
    
}