package gov.nist.healthcare.iz.darq.digest.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.iz.darq.digest.domain.ADChunk;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Fraction;
import gov.nist.healthcare.iz.darq.digest.service.ConfigurationProvider;
import gov.nist.healthcare.iz.darq.digest.service.DigestRunner;
import gov.nist.healthcare.iz.darq.digest.service.MergeService;
import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;
import gov.nist.lightdb.domain.EntityTypeRegistry;
import gov.nist.lightdb.service.LightTextDB;
import gov.nist.lightdb.service.LightWeightIndexer;
import gov.nist.lightdb.service.ObjectComposer;
import gov.nist.lightdb.service.LightWeigthSearcher.ChunkItemIterator;

@Service
public class ParallelDigestRunner implements DigestRunner {

	@Autowired
	ObjectComposer<AggregatePatientRecord> composer;
	@Autowired
	LightWeightIndexer.Builder indexer;
	final int THREADS = 3;
	ExecutorService executor = Executors.newFixedThreadPool(THREADS);
	@Autowired
	SimpleRecordChewer chewer;
	@Autowired
	MergeService merge;
	int size;
	List<ChunkItemIterator<AggregatePatientRecord>> parts;
	
	@Override
	public ADChunk digest(ConfigurationPayload configuration, String patient, String vaccines) throws Exception {
		LocalDate now = new LocalDate();
		parts = chunks(patient, vaccines);
		final ADChunk file = new ADChunk();
		size = parts.stream().map(x->{
			return x.getSize();
		}).reduce( (x, y) -> {
			return x + y;
		}).get();
		ConfigurationProvider config = new SimpleConfigurationProvider(configuration);
		for(ChunkItemIterator<AggregatePatientRecord> iterator : parts){
			Callable<Void> r = ()->{
				
					while(iterator.hasNext()){
						try {
							AggregatePatientRecord apr = iterator.nextRecord();
							merge.mergeChunk(file, chewer.munch(config, apr, now));
						}
						catch(Exception e){
							file.incUnread();
							file.getIssues().add(e.getMessage());
//							e.printStackTrace();
						}
					}
				
				
				return null;
			};
			executor.submit(r);
		}
		
		executor.shutdown();
		executor.awaitTermination(2, TimeUnit.DAYS);
		return file;
	}
	
	
	private List<ChunkItemIterator<AggregatePatientRecord>> chunks(String ppath, String vpath) throws Exception {
		try(
				InputStream patient = new FileInputStream(new File(ppath));
				InputStream vaccines = new FileInputStream(new File(vpath));
		) {
			File dir = com.google.common.io.Files.createTempDir();
			if (dir.exists()) {
				String dirPath = Paths.get(dir.getAbsolutePath()).toString();

				FileUtils.copyInputStreamToFile(patient, Paths.get(dir.getAbsolutePath(), "data",  EntityTypeRegistry.get("patient").i.file).toFile());
				FileUtils.copyInputStreamToFile(vaccines, Paths.get(dir.getAbsolutePath(), "data",  EntityTypeRegistry.get("vaccination").i.file).toFile());
				Paths.get(dir.getAbsolutePath(), "index").toFile().mkdirs();
				
				LightTextDB db = LightTextDB.refresh(dirPath, EntityTypeRegistry.get("patient"), indexer.build(), EntityTypeRegistry.get("vaccination"));
				
				
				db.addComposer(AggregatePatientRecord.class, composer);
				return db.chunkIterator(AggregatePatientRecord.class, THREADS);
			}
			throw new Exception("Unable to create temporary directory");
		}
	}


	@Override
	public Fraction spy() {
		if(this.parts == null){
			return new Fraction(0,0);
		}
		else {
			float total = parts.stream().map(x->{
				return x.records();
			}).reduce( (x, y) -> {
				return x + y;
			}).get();
			return new Fraction((int) total, size);
		}		
	}

}
