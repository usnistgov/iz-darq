package gov.nist.healthcare.iz.darq.batch.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import gov.nist.healthcare.iz.darq.analysis.AnalysisRawResult;
import gov.nist.healthcare.iz.darq.batch.domain.JobData;

public interface JobManager {

	public JobData createJob(JobData data, InputStream patient, InputStream vaccines, String user) throws IOException, Exception; 
	public List<JobData> getJobs(String user); 
	public AnalysisRawResult resultsFor(String id, String user);
	boolean delete(String id, String user);
	boolean stop(String id);
	
}
