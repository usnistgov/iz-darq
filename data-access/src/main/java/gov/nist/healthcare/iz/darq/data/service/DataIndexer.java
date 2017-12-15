package gov.nist.healthcare.iz.darq.data.service;

public interface DataIndexer {
	
	public void index(JobDataToken token) throws Exception;

}
