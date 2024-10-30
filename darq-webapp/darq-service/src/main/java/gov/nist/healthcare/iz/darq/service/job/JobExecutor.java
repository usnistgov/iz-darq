package gov.nist.healthcare.iz.darq.service.job;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class JobExecutor {
	ThreadPoolExecutor executor;

	@Value("${job.executor.threads}")
	private int threads;
	private static final int DEFAULT_THREAD_COUNT = 5;

	@PostConstruct
	public void initialize() {
		int nThreads = threads > 0 ? threads : DEFAULT_THREAD_COUNT;
		this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
	}

	@PreDestroy
	public void shutdown() {
		this.executor.shutdownNow();
	}

	public Future<?> submit(RunnableJob job) {
		return executor.submit(job);
	}

	public boolean queueContainsJob(String jobId) {
		return this.executor.getQueue().stream().anyMatch((j) -> ((RunnableJob) j).getJob().getId().equals(jobId));
	}
}
