package gov.nist.healthcare.iz.darq.batch.service;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import gov.nist.healthcare.iz.darq.batch.domain.Job;

public class JobThreadPoolExecutor extends ThreadPoolExecutor {

	private Job active;

	public JobThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new LinkedBlockingQueue<Runnable>());
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		Job job = (Job) JobDiscovery.findRealTask(r);
		System.out.println(job.getId());
		t.setName(job.getId());
		job.start();
		this.active = job;
		super.beforeExecute(t, r);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		System.out.println("HTHERE");
		System.out.println(t);

		if (t != null) {
			this.active.fail();
		} else {
			if (r instanceof Future) {
				try {
					((Future) r).get();
				} catch (CancellationException ce) {
					t = ce;
				} catch (ExecutionException ee) {
					t = ee.getCause();
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt(); // ignore/reset
				}
				catch (Exception e) {
					t = e;
				}
			}
		}
		
		if (t == null) {
			this.active.end();
		} else {
			this.active.fail();
		}
		this.active = null;
		super.afterExecute(r, t);
	}

	public Job getActive() {
		return active;
	}

}
