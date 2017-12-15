package gov.nist.healthcare.iz.darq.batch.service;

import java.util.concurrent.Future;
import gov.nist.healthcare.iz.darq.batch.domain.Job;

public interface JobExecutor {
	
	public Future<Void> run(Job job);
	public Job running();
	
}
