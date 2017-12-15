package gov.nist.healthcare.iz.darq.batch.service;

import gov.nist.healthcare.iz.darq.analysis.AnalysisRawResult;
import gov.nist.healthcare.iz.darq.batch.domain.JobData;

public abstract class AnalysisReporter {
	
	public abstract AnalysisRawResult get(JobData j);
	public abstract void report(JobData j, AnalysisRawResult result);
	public void commit(JobData j, AnalysisRawResult result){
		AnalysisRawResult data = this.get(j);
		if(data != null){
			data.factorIn(result);
			this.report(j, data);
		}
		else {
			this.report(j, result);
		}
	}

}
