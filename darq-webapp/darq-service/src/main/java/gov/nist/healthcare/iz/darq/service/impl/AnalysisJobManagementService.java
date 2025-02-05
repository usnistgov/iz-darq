package gov.nist.healthcare.iz.darq.service.impl;

import com.google.common.base.Strings;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisReport;
import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportTemplate;
import gov.nist.healthcare.iz.darq.analyzer.service.ReportService;
import gov.nist.healthcare.iz.darq.model.AnalysisJob;
import gov.nist.healthcare.iz.darq.model.JobStatus;
import gov.nist.healthcare.iz.darq.model.UserUploadedFile;
import gov.nist.healthcare.iz.darq.repository.AnalysisJobRepository;
import gov.nist.healthcare.iz.darq.repository.TemplateRepository;
import gov.nist.healthcare.iz.darq.service.job.RunnableJob;
import gov.nist.healthcare.iz.darq.service.domain.AnalysisJobCreateData;
import gov.nist.healthcare.iz.darq.service.exception.JobRunningException;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.service.job.JobExecutor;
import gov.nist.healthcare.iz.darq.service.job.JobManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Service
public class AnalysisJobManagementService implements JobManagementService<AnalysisJob, AnalysisJobCreateData> {

	Map<String, Future<?>> futures = new ConcurrentHashMap<>();
	@Autowired
	TemplateRepository templateRepository;
	@Autowired
	AnalysisJobRepository analysisJobRepository;
	@Autowired
	AnalysisReportService analysisReportService;
	@Autowired
	ReportService analysisService;
	@Autowired
	ADFStorage store;
	@Autowired
	JobExecutor executor;

	@Override
	public AnalysisJob add(AnalysisJobCreateData jobCreateData) throws JobRunningException, NotFoundException {
		ReportTemplate template = this.templateRepository.findByOwnerIdOrReadOnly(jobCreateData.getTemplateId(), jobCreateData.getOwnerId());
		UserUploadedFile adf = this.store.get(jobCreateData.getAdfId());
		if(template == null) {
			throw new JobRunningException("Report Template" + jobCreateData.getTemplateId() + " Not Found");
		}

		if(adf == null) {
			throw new JobRunningException("ADF " + jobCreateData.getAdfId() + " Not Found");
		}

		if(jobCreateData.getFacilityId() != null && !jobCreateData.getFacilityId().equals(adf.getFacilityId())) {
			throw new JobRunningException("Facility Id "+ jobCreateData.getFacilityId() +" does not match the ADF's facility Id "+ adf.getFacilityId());
		}

		AnalysisJob job = new AnalysisJob(jobCreateData.getName(),
		                                  jobCreateData.getAdfId(),
		                                  adf.getName(),
		                                  template,
		                                  null, jobCreateData.getOwnerId(),
		                                  adf.getFacilityId());
		job.setSubmitTime(new Date());
		this.analysisJobRepository.save(job);
		Future<?> future = executor.submit(new RunnableJob(job, this, (e) -> {
			futures.remove(job.getId());
			return null;
		}));
		this.register(job.getId(), future);
		return job;
	}

	@Override
	public AnalysisJob start(AnalysisJob job) throws Exception {
		if(job.getStatus() == JobStatus.RUNNING) {
			throw new JobRunningException("Job " + job.getName() + " Already Running ");
		} else {
			job.setStatus(JobStatus.RUNNING);
			job.setStartTime(new Date());
			this.analysisJobRepository.save(job);
		}

		try(ADFReader file = this.store.getFile(job.getAdfId())) {
			AnalysisReport report = this.analysisService.analyse(file, job.getTemplate(), job.getFacilityId());
			file.close();
			report.fromTemplate(job.getTemplate());
			report.setName(job.getName());
			report.setOwner(job.getOwner());
			report.setOwnerId(job.getOwnerId());
			report.setFacilityId(job.getFacilityId());
			report.setAdfName(job.getAdfName());
			report.setReportTemplate(job.getTemplate());
			this.analysisReportService.save(report);
			job.setReportId(report.getId());
			job.setEndTime(new Date());
			job.setStatus(JobStatus.FINISHED);
			this.analysisJobRepository.save(job);
			this.clear(job.getId());
			return job;
		}
	}

	@Override
	public AnalysisJob fail(AnalysisJob job, String reason) {
		job.setStatus(JobStatus.FAILED);
		job.setEndTime(new Date());
		job.setFailure(reason);
		this.analysisJobRepository.save(job);
		this.clear(job.getId());
		return job;
	}

	@Override
	public List<AnalysisJob> getAllJobsForUserAndFacility(String ownerId, String facilityId) {
		if (facilityId == null) {
			return this.analysisJobRepository.findByOwnerIdAndFacilityIdIsNull(ownerId);
		} else {
			return this.analysisJobRepository.findByOwnerIdAndFacilityId(ownerId, facilityId);
		}
	}

	@Override
	public List<AnalysisJob> updateStaleRunningAndQueuedJobStatusOnStartup() {
		List<AnalysisJob> jobs = this.analysisJobRepository.findByStatusIn(Arrays.asList(JobStatus.RUNNING, JobStatus.QUEUED));
		for(AnalysisJob job: jobs) {
			this.fail(job, "Job incorrectly stopped, perhaps due to application shutdown");
		}
		return jobs;
	}

	@Override
	public boolean delete(String id) throws JobRunningException {
		if(this.futures.containsKey(id)) {
			Future<?> future = this.futures.get(id);
			if(future.isDone() || future.isCancelled()) {
				AnalysisJob job = this.analysisJobRepository.findOne(id);
				if(job != null) {
					if(! Strings.isNullOrEmpty(job.getReportId())) {
						this.analysisReportService.delete(job.getReportId());
					}
					this.analysisJobRepository.delete(id);
				} else {
					this.clear(id);
					throw new JobRunningException("Job does not exist");
				}
				this.clear(id);
				return true;
			} else {
				throw new JobRunningException("Can't delete running task");
			}
		} else {
			AnalysisJob job = this.analysisJobRepository.findOne(id);
			if(job != null) {
				if(!Strings.isNullOrEmpty(job.getReportId())) {
					this.analysisReportService.delete(job.getReportId());
				}
				this.analysisJobRepository.delete(id);
				return true;
			} else {
				throw new JobRunningException("Job does not exist");
			}
		}
	}

	public void register(String id, Future<?> future) {
		this.futures.put(id, future);
	}

	public void clear(String id) {
		this.futures.remove(id);
	}
}
