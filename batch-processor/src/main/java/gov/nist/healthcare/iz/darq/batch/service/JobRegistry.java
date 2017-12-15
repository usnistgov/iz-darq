package gov.nist.healthcare.iz.darq.batch.service;

import java.util.List;

import gov.nist.healthcare.iz.darq.batch.domain.JobData;

public interface JobRegistry {

	JobData save(JobData data);
	JobData delete(String id);
	JobData get(String id);
	List<JobData> getJobs(String user);
	
}
