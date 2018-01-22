package gov.nist.healthcare.iz.darq.batch.domain;

import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import gov.nist.healthcare.iz.darq.analysis.AnalysisRawResult;
import gov.nist.healthcare.iz.darq.analysis.DataQualityProcessor;
import gov.nist.healthcare.iz.darq.analysis.stats.StatisticsProcessor;
import gov.nist.healthcare.iz.darq.batch.service.AnalysisReporter;
import gov.nist.healthcare.iz.darq.batch.service.CallableJob;
import gov.nist.healthcare.iz.darq.batch.service.JobRegistry;
import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;
import gov.nist.lightdb.domain.EntityTypeRegistry;
import gov.nist.lightdb.service.LightTextDB;
import gov.nist.lightdb.service.LightWeightIndexer;
import gov.nist.lightdb.service.ObjectComposer;
import gov.nist.lightdb.service.LightWeigthSearcher.ChunkItemIterator;

public class Job implements CallableJob {
	
	private final int THREADS = 3;
	private JobData data;
	private LightWeightIndexer indexer;
	private JobRegistry registry;
	private List<ChunkItemIterator<AggregatePatientRecord>> chunks;
	private ObjectComposer<AggregatePatientRecord> composer;
	private ExecutorService executor;
	private int size = 0;
	private boolean stoped = false;
	private StatisticsProcessor statsProcessor;
	private AnalysisReporter reporter;
	private DataQualityProcessor dqaProcessor;
	private final int records_before_commit = 1000;
	
	public Job(JobData data, ObjectComposer<AggregatePatientRecord> composer,  LightWeightIndexer indexer, JobRegistry registry, StatisticsProcessor statsCalculator, DataQualityProcessor dqa, AnalysisReporter reporter) {
		this.composer = composer;
		this.data = data;
		this.registry = registry;
		this.indexer = indexer;
		this.dqaProcessor = dqa;
		this.executor = Executors.newFixedThreadPool(THREADS);
		this.statsProcessor = statsCalculator;
		this.reporter = reporter;
	}
	
	public String getId(){
		return data.getId();
	}
	
	public JobData save(){
		data.setDateLastUpdated(new Date());
		return this.registry.save(this.data);
	}
	
	public Job updateStatus(JobStatus status){
		this.data.setStatus(status);
		return this;
	}
	
	@Override
	public float progress() {
		if(chunks == null){
			return 0;
		}

		float total = chunks.stream().map(x->{
			return x.records();
		}).reduce( (x, y) -> {
			return x + y;
		}).get();
		
		return total / size;
	}
	
	@Override
	public void start(){
		this.data.setDateStarted(new Date());
		this.data.setStatus(JobStatus.STARTED);
		this.save();
	}
	
	@Override
	public void end(){
		this.data.setDateEnded(new Date());
		this.data.setStatus(JobStatus.FINISHED);
		this.data.setProgress(1);
		this.save();
	}
	
	@Override
	public void fail(){
		this.data.setDateEnded(new Date());
		this.data.setStatus(JobStatus.FAILED);
		this.data.setProgress(progress());
		this.save();
	}
	
	public void stop(){
		this.stoped = true;
		this.data.setDateEnded(new Date());
		this.data.setStatus(JobStatus.STOPPED);
		this.data.setProgress(progress());
		this.save();
	}
	
	@Override
	public Void call() throws Exception {
		LightTextDB db = LightTextDB.refresh(this.data.getMount(), EntityTypeRegistry.get("patient"), indexer, EntityTypeRegistry.get("vaccination"));
	
		
		db.addComposer(AggregatePatientRecord.class, composer);
		chunks = db.chunkIterator(AggregatePatientRecord.class, THREADS);
		size = chunks.stream().map(x->{
			return x.getSize();
		}).reduce( (x, y) -> {
			return x + y;
		}).get();
		
		for(ChunkItemIterator<AggregatePatientRecord> iterator : chunks){
			Callable<Void> r = ()->{
				int i = 0;
				AnalysisRawResult results = new AnalysisRawResult();
				
				while(iterator.hasNext() && !this.stoped){
					AggregatePatientRecord apr = iterator.next();
					apr.ID = apr.patient.patID.getValue();
					try {
						this.statsProcessor.process(results.getFields(), apr);
						results.getDetections().addAll(this.dqaProcessor.inspect(apr));
					}
					catch(Exception e){
						e.printStackTrace();
					}
					
					i++;
					results.setTotalRecords(i);
					
					if(results.getTotalRecords() >= this.records_before_commit){
						commitResult(results);
						i = 0;
						results = new AnalysisRawResult();
					}
				}
				
				if(i != 0){
					commitResult(results);
				}
				return null;
			};
			this.executor.submit(r);
		}
		this.executor.shutdown();
		this.executor.awaitTermination(2, TimeUnit.DAYS);
		FileUtils.deleteDirectory(Paths.get(data.getMount()).toFile());
		Runtime.getRuntime().gc();
		return null;
	}
	
	public synchronized void commitResult(AnalysisRawResult results){
		this.reporter.commit(this.data, results);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Job other = (Job) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}


}
