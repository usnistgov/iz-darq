package gov.nist.healthcare.iz.darq.service.impl;

import gov.nist.healthcare.iz.darq.model.ADFMergeJob;
import gov.nist.healthcare.iz.darq.model.JobStatus;
import gov.nist.healthcare.iz.darq.model.UserUploadedFile;
import gov.nist.healthcare.iz.darq.repository.ADFMergeJobRepository;
import gov.nist.healthcare.iz.darq.service.job.JobExecutor;
import gov.nist.healthcare.iz.darq.service.job.RunnableJob;
import gov.nist.healthcare.iz.darq.service.domain.ADFMergeJobCreateData;
import gov.nist.healthcare.iz.darq.service.exception.JobRunningException;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.service.job.JobManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Service
public class ADFMergeJobManagementService implements JobManagementService<ADFMergeJob, ADFMergeJobCreateData> {

	Map<String, Future<?>> futures = new ConcurrentHashMap<>();
	@Autowired
	ADFMergeJobRepository adfMergeJobRepository;
	@Autowired
	JobExecutor executor;
	@Autowired
	ADFService adfService;
	@Autowired
	ADFStorage store;

	@Override
	public ADFMergeJob add(ADFMergeJobCreateData data) throws JobRunningException, NotFoundException {
		ADFMergeJob job = new ADFMergeJob(data.getName(),
		                                  data.getTags(),
		                                  data.getIds(),
										   null, data.getOwnerId(),
		                                  data.getFacilityId());
		job.setSubmitTime(new Date());
		this.adfMergeJobRepository.save(job);
		Future<?> future = executor.submit(new RunnableJob(job, this, (e) -> {
			futures.remove(job.getId());
			return null;
		}));
		this.register(job.getId(), future);
		return job;
	}

	@Override
	public ADFMergeJob start(ADFMergeJob job) throws Exception {
		if(job.getStatus() == JobStatus.RUNNING) {
			throw new JobRunningException("Job " + job.getName() + " Already Running ");
		} else {
			job.setStatus(JobStatus.RUNNING);
			job.setStartTime(new Date());
			this.adfMergeJobRepository.save(job);
		}
		Thread.sleep(10000);
		Set<UserUploadedFile> files = new HashSet<>();
		for(String id: job.getIds()) {
			UserUploadedFile file = this.store.get(id);
			if(file == null) {
				throw new Exception("Not found ADF with Id = "+ id);
			} else {
				files.add(file);
			}
		}

		UserUploadedFile merged = this.adfService.merge(job.getName(), job.getTags(), job.getFacilityId(), job.getOwnerId(), files);
		job.setEndTime(new Date());
		job.setStatus(JobStatus.FINISHED);
		job.setMergedAdfId(merged.getId());
		this.adfMergeJobRepository.save(job);
		this.clear(job.getId());
		return job;
	}

	@Override
	public ADFMergeJob fail(ADFMergeJob job, String reason) {
		job.setStatus(JobStatus.FAILED);
		job.setEndTime(new Date());
		job.setFailure(reason);
		this.adfMergeJobRepository.save(job);
		this.clear(job.getId());
		return job;
	}

	@Override
	public List<ADFMergeJob> getAllJobsForUserAndFacility(String ownerId, String facilityId) {
		if (facilityId == null) {
			return this.adfMergeJobRepository.findByOwnerIdAndFacilityIdIsNull(ownerId);
		} else {
			return this.adfMergeJobRepository.findByOwnerIdAndFacilityId(ownerId, facilityId);
		}
	}

	@Override
	public List<ADFMergeJob> updateStaleRunningAndQueuedJobStatusOnStartup() {
		List<ADFMergeJob> jobs = this.adfMergeJobRepository.findByStatusIn(Arrays.asList(JobStatus.RUNNING, JobStatus.QUEUED));
		for(ADFMergeJob job: jobs) {
			this.fail(job, "Job incorrectly stopped, perhaps due to application shutdown");
		}
		return jobs;
	}

	@Override
	public boolean delete(String id) throws JobRunningException {
		if(this.futures.containsKey(id)) {
			Future<?> future = this.futures.get(id);
			if(future.isDone() || future.isCancelled()) {
				ADFMergeJob job = this.adfMergeJobRepository.findOne(id);
				if(job != null) {
					this.adfMergeJobRepository.delete(id);
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
			ADFMergeJob job = this.adfMergeJobRepository.findOne(id);
			if(job != null) {
				this.adfMergeJobRepository.delete(id);
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
