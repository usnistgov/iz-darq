package gov.nist.healthcare.iz.darq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.nist.healthcare.iz.darq.analysis.AnalysisRawResult;
import gov.nist.healthcare.iz.darq.batch.domain.JobData;
import gov.nist.healthcare.iz.darq.batch.service.AnalysisReporter;
import gov.nist.healthcare.iz.darq.repository.AnalysisResultRepository;
import gov.nist.healthcare.iz.darq.repository.JobDataRepository;

@Component
public class MongoAnalysisReporter extends AnalysisReporter {

	@Autowired
	private AnalysisResultRepository arRepo;
	@Autowired
	private JobDataRepository jdRepo;
	
	@Override
	public AnalysisRawResult get(JobData j) {
		if(j.getResultId() != null && !j.getResultId().isEmpty())
		return arRepo.findOne(j.getResultId());
		
		return null;
	}

	@Override
	public void report(JobData j, AnalysisRawResult result) {
		boolean first = result.getId() == null || result.getId().isEmpty();
		arRepo.save(result);
		
		if(first){
			j.setResultId(result.getId());
			this.jdRepo.save(j);
		}
	}

}
