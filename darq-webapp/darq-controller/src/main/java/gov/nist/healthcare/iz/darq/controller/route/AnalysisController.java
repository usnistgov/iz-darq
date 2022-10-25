package gov.nist.healthcare.iz.darq.controller.route;

import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.access.security.CustomSecurityExpressionRoot;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.DataTable;
import gov.nist.healthcare.iz.darq.analyzer.model.template.DataViewQuery;
import gov.nist.healthcare.iz.darq.analyzer.model.template.QueryPayload;
import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportTemplate;
import gov.nist.healthcare.iz.darq.controller.domain.JobCreation;
import gov.nist.healthcare.iz.darq.controller.service.DescriptorService;
import gov.nist.healthcare.iz.darq.model.*;
import gov.nist.healthcare.iz.darq.repository.ADFMetaDataRepository;
import gov.nist.healthcare.iz.darq.repository.DigestConfigurationRepository;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;
import gov.nist.healthcare.iz.darq.service.analysis.AnalysisJobRunner;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import gov.nist.healthcare.iz.darq.service.utils.ConfigurationService;
import gov.nist.healthcare.iz.darq.users.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import gov.nist.healthcare.iz.darq.adf.service.ADFStore;
import gov.nist.healthcare.iz.darq.analyzer.service.ReportService;
import gov.nist.healthcare.iz.darq.repository.TemplateRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

	@Autowired
	private ReportService report;
	@Autowired
	private ADFStore storage;
	@Autowired
	private AnalysisJobRunner runner;
	@Autowired
	private DescriptorService descriptorService;
	@Autowired
	private ConfigurationService configurationService;
	@Autowired
	private DigestConfigurationRepository configurationRepository;


	@RequestMapping(value = "/query/{fId}", method = RequestMethod.POST)
	@ResponseBody
	@PreAuthorize("AccessResource(#request, ADF, VIEW, #fId)")
	public DataTable analyze(
			HttpServletRequest request,
			@RequestBody QueryPayload query,
			@PathVariable("fId") String fId) throws Exception {
		UserUploadedFile metadata = (UserUploadedFile) request.getAttribute(CustomSecurityExpressionRoot.RESOURCE_ATTRIBUTE);
		ADFile file = this.storage.getFile(fId);
    	if(file != null){
			return this.report.singleQuery(file, query, metadata.getFacilityId());
    	} else {
    		throw new NotFoundException(" ADF File "+fId+" Not Found");
		}
	}

	@RequestMapping(value = "/job", method = RequestMethod.POST)
	@ResponseBody
	@PreAuthorize("AccessResource(ADF, ANALYSE, #job.adfId)")
	public OpAck<AnalysisJobDescriptor> submitJob(
			@AuthenticationPrincipal User user,
			@RequestBody JobCreation job) throws Exception {
		AnalysisJob submitted = this.runner.addJob(job.getName(), job.getTemplateId(), job.getAdfId(), user.getId());
		return new OpAck<>(OpAck.AckStatus.SUCCESS, "Job Submitted Successfully", getAnalysisJobDescriptor(submitted, user), "job-submit");
	}

	@RequestMapping(value = {"/jobs/{facilityId}"}, method = RequestMethod.GET)
	@ResponseBody
	@PreAuthorize("AccessOperation(ANALYSIS_JOB, VIEW, FACILITY(#facilityId), OWNED())")
	public List<AnalysisJobDescriptor> getJobsForFacility(
			@AuthenticationPrincipal User user,
			@PathVariable("facilityId") String facilityId) {
		return getAnalysisJobDescriptorList(this.runner.getAllJobsForUserAndFacility(user.getId(), facilityId), user);
	}

	@RequestMapping(value = {"/jobs"}, method = RequestMethod.GET)
	@ResponseBody
	@PreAuthorize("AccessOperation(ANALYSIS_JOB, VIEW, GLOBAL, OWNED())")
	public List<AnalysisJobDescriptor> getJobsForUser(
			@AuthenticationPrincipal User user)  {
		return getAnalysisJobDescriptorList(this.runner.getAllJobsForUserAndFacility(user.getId(), null), user);
	}

	@RequestMapping(value = "/job/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	@PreAuthorize("AccessResource(ANALYSIS_JOB, DELETE, #id)")
	public OpAck<AnalysisJob> deleteJob(
			@PathVariable("id") String id) throws Exception {
		if(this.runner.deleteJob(id)) {
			return new OpAck<>(OpAck.AckStatus.SUCCESS, "Job Deleted Successfully", null, "job-delete");
		} else {
			throw new OperationFailureException("Failed to delete Job");
		}
	}

	AnalysisJobDescriptor getAnalysisJobDescriptor(AnalysisJob job, User user) {
		List<DigestConfiguration> configurations = this.configurationRepository.findAccessibleTo(user.getId());
		return this.descriptorService.getAnalysisJobDescriptor(job, this.configurationService.compatibilities(job.getTemplate().getConfiguration(), configurations));
	}

	List<AnalysisJobDescriptor> getAnalysisJobDescriptorList(List<AnalysisJob> jobs, User user) {
		List<AnalysisJobDescriptor> result = new ArrayList<>();
		List<DigestConfiguration> configurations = this.configurationRepository.findAccessibleTo(user.getId());
		for(AnalysisJob job : jobs){
			result.add(this.descriptorService.getAnalysisJobDescriptor(job, this.configurationService.compatibilities(job.getTemplate().getConfiguration(), configurations)));
		}
		return result;
	}


    
}