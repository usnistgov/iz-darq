package gov.nist.healthcare.iz.darq.batch.service;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import gov.nist.healthcare.iz.darq.batch.domain.Job;

public class SimpleJobExecutor implements JobExecutor {

	private JobThreadPoolExecutor executor;
	
	public SimpleJobExecutor(int size) {
		super();
		this.executor = new JobThreadPoolExecutor(size, size, 10, TimeUnit.SECONDS);
	}

	@Override
	public Future<Void> run(Job job) {
		System.out.println(job);
		return this.executor.submit(job);
	}

	@Override
	public Job running() {
		return this.executor.getActive();
	}

}
