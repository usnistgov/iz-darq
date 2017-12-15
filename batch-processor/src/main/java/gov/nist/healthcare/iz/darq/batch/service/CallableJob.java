package gov.nist.healthcare.iz.darq.batch.service;

import java.util.concurrent.Callable;

public interface CallableJob extends Callable<Void> {
	
	public float progress();
	public void start();
	public void end();
	public void fail();
	public void stop();
	
}
