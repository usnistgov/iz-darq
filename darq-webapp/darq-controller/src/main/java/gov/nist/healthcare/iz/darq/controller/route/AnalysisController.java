package gov.nist.healthcare.iz.darq.controller.route;


import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.DataTable;
import gov.nist.healthcare.iz.darq.analyzer.model.template.DataViewQuery;
import gov.nist.healthcare.iz.darq.controller.domain.JobCreation;
import gov.nist.healthcare.iz.darq.controller.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;
import gov.nist.healthcare.iz.darq.model.AnalysisJob;
import gov.nist.healthcare.iz.darq.service.analysis.AnalysisJobRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import gov.nist.healthcare.auth.service.AccountService;
import gov.nist.healthcare.iz.darq.adf.service.ADFStore;
import gov.nist.healthcare.iz.darq.analyzer.service.ReportService;
import gov.nist.healthcare.iz.darq.repository.TemplateRepository;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

	@Autowired
	private AccountService accountService;
	@Autowired
	private TemplateRepository templateRepo;
	@Autowired
	private ReportService report;
	@Autowired
	private ADFStore storage;
	@Autowired
	private AnalysisJobRunner runner;


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

	@RequestMapping(value = "/job", method = RequestMethod.POST)
	@ResponseBody
	public OpAck<AnalysisJob> submitJob(@RequestBody JobCreation job) throws Exception {
		Account a = this.accountService.getCurrentUser();
		AnalysisJob submitted = this.runner.addJob(job.getName(), job.getTemplateId(), job.getAdfId(), a.getUsername());
		return new OpAck<>(OpAck.AckStatus.SUCCESS, "Job Submitted Successfully", submitted, "job-submit");
	}

	@RequestMapping(value = "/jobs", method = RequestMethod.GET)
	@ResponseBody
	public List<AnalysisJob> getJobs() throws Exception {
		Account a = this.accountService.getCurrentUser();
		return this.runner.getAllJobsForUser(a.getUsername());
	}

	@RequestMapping(value = "/job/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public OpAck<AnalysisJob> getJobs(@PathVariable("id") String id) throws Exception {
		Account a = this.accountService.getCurrentUser();
		if(this.runner.deleteJobForUser(id, a.getUsername())) {
			return new OpAck<>(OpAck.AckStatus.SUCCESS, "Job Deleted Successfully", null, "job-delete");
		} else {
			return new OpAck<>(OpAck.AckStatus.FAILED, "Failed to delete Job", null, "job-delete");
		}
	}


    
}