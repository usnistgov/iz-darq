package gov.nist.healthcare.iz.darq.service.job;

import gov.nist.healthcare.iz.darq.model.Job;
import gov.nist.healthcare.iz.darq.service.domain.CreateJobData;
import gov.nist.healthcare.iz.darq.service.exception.JobRunningException;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;

import java.util.List;

public interface JobManagementService<T extends Job, E extends CreateJobData> {
	T add(E data) throws JobRunningException, NotFoundException;
	T start(T job) throws Exception;
	T fail(T job, String reason);
	List<T> getAllJobsForUserAndFacility(String user, String facilityId);
	List<T> updateStaleRunningAndQueuedJobStatusOnStartup();
	boolean delete(String id) throws JobRunningException;
}
