package gov.nist.healthcare.iz.darq.batch.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.nist.healthcare.iz.darq.analysis.AnalysisRawResult;
import gov.nist.healthcare.iz.darq.batch.domain.Job;
import gov.nist.healthcare.iz.darq.batch.domain.JobData;
import gov.nist.healthcare.iz.darq.batch.domain.JobStatus;
import gov.nist.lightdb.domain.EntityTypeRegistry;

@Component
public class SimpleJobManager implements JobManager {

	@Autowired
	private JobRegistry registry;
	@Autowired
	private JobExecutor executor;
	@Autowired 
	private JobFactory factory;
	@Autowired
	private AnalysisReporter reporter;

	@Override
	public JobData createJob(JobData data, InputStream patient, InputStream vaccines, String user) throws Exception {
		File dir = com.google.common.io.Files.createTempDir();
		if (dir.exists()) {
			data.setMount(Paths.get(dir.getAbsolutePath()).toString());
			data.setStatus(JobStatus.QUEUED);
			data.setProgress(0);
			data.setDateCreated(new Date());
			data.setDateLastUpdated(new Date());
			data.setUser(user);
			FileUtils.copyInputStreamToFile(patient, Paths.get(dir.getAbsolutePath(), "data",  EntityTypeRegistry.get("patient").i.file).toFile());
			FileUtils.copyInputStreamToFile(vaccines, Paths.get(dir.getAbsolutePath(), "data",  EntityTypeRegistry.get("vaccination").i.file).toFile());
			Paths.get(dir.getAbsolutePath(), "index").toFile().mkdirs();
			Job job = factory.getJob(data);
			job.save();
			this.executor.run(job);
			return data;
		} else {
			throw new IOException();
		}
	}

	@Override
	public List<JobData> getJobs(String user) {
		List<JobData> jobs = this.registry.getJobs(user);
		if(this.executor.running() == null)
			return jobs;
		
		String id = this.executor.running().getId();
		Optional<JobData> jobData = jobs.stream().filter(x->x.getId().equals(id)).findFirst();
		if(jobData.isPresent()){
			jobData.get().setProgress(round(executor.running().progress(),2));
		}
		return jobs;
	}

	@Override
	public AnalysisRawResult resultsFor(String id, String user) {
		JobData jd = this.registry.get(id);
		if(jd != null && jd.getUser().equals(user)){
			return reporter.get(jd);
		}
		return null;
	}
	
	@Override
	public boolean delete(String id, String user) {
		JobData jd = this.registry.get(id);
		if(jd != null && jd.getUser().equals(user)){
			registry.delete(id);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean stop(String id) {
		if(this.executor.running() != null){
			if(this.executor.running().getId().equals(id)){
				this.executor.running().stop();
				return true;
			}
		}
		return false;
	}
	
	public static float round(float number, int scale) {
	    int pow = 10;
	    for (int i = 1; i < scale; i++)
	        pow *= 10;
	    float tmp = number * pow;
	    return ( (float) ( (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) ) ) / pow;
	}

}
