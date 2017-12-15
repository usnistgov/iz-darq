package gov.nist.healthcare.iz.darq.batch.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import gov.nist.healthcare.iz.darq.analysis.stats.StatisticsProcessor;
import gov.nist.healthcare.iz.darq.batch.domain.Job;
import gov.nist.healthcare.iz.darq.batch.domain.JobData;
import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;
import gov.nist.lightdb.service.LightWeightIndexer;
import gov.nist.lightdb.service.ObjectComposer;

@Component
public class JobFactory {

	@Autowired
	private ObjectComposer<AggregatePatientRecord> composer;
	@Autowired
	private LightWeightIndexer.Builder indexer;
	@Autowired
	private JobRegistry registry;
	@Autowired
	private StatisticsProcessor stats;
	@Autowired
	private AnalysisReporter reporter;
	
	public Job getJob(JobData data) {
		return new Job(data, composer, indexer.build(), registry, stats, reporter);
	}
}
