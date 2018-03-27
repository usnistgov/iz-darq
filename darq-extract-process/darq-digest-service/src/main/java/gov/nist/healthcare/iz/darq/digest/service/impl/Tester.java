package gov.nist.healthcare.iz.darq.digest.service.impl;

//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Paths;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
//import org.apache.commons.io.FileUtils;
//import org.joda.time.LocalDate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

//import com.fasterxml.jackson.core.JsonGenerationException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import gov.nist.healthcare.iz.darq.digest.domain.ADChunk;
//import gov.nist.healthcare.iz.darq.digest.service.AgeGroupService;
//import gov.nist.healthcare.iz.darq.digest.service.MergeService;
//import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;
//import gov.nist.healthcare.iz.darq.parser.model.Patient;
//import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;
//import gov.nist.healthcare.iz.darq.parser.service.PatientParser;
//import gov.nist.healthcare.iz.darq.parser.service.VaccinationParser;
//import gov.nist.lightdb.domain.EntityTypeRegistry;
//import gov.nist.lightdb.exception.InvalidValueException;
//import gov.nist.lightdb.service.LightTextDB;
//import gov.nist.lightdb.service.LightWeightIndexer;
//import gov.nist.lightdb.service.ObjectComposer;
//import gov.nist.lightdb.service.LightWeigthSearcher.ChunkItemIterator;

@Configuration
@ComponentScan("gov.nist.healthcare")
public class Tester {


	
//	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InvalidValueException, JsonGenerationException, JsonMappingException, IOException {
//		PatientParser patientParser = new PatientParser();;
//		VaccinationParser vaccinationParser = new VaccinationParser();
//		String patient = "U79558507	Hiten	Kalidas	Hall	Allayna	Inge	Ronni	Cameron	19520728	M	74 Amador Cir	Utica	MI	USA	48317	1	0	(586)4978100	lawson.multnomah@madeupemailaddress.com	eng	Snorre	Semaj	Custer	Olien	Aislinn	Jackson	Mother	GreenleeFacility	Yes	1	[[NOT_EXTRACTED]]	Active";
//	    List<String> vaccinations = Arrays.asList(
//	    	"U79558507	03	[[NOT_EXTRACTED]]	20131126	LilleyCorporation	F42316191	00	[[NOT_EXTRACTED]]	F54	Nasal	Right Arm	20160921	0,5	Praful	Cornelius	Johnson	[[NOT_EXTRACTED]]	[[NOT_EXTRACTED]]	[[NOT_EXTRACTED]]	vo1	CP"
//	    );
//	    
//	    Patient p = patientParser.parse(patient);
//	    List<VaccineRecord> history = vaccinations.stream().map(line -> {
//			try {
//				return vaccinationParser.parse(line);
//			} catch (InvalidValueException e) {
//				return null;
//			}
//		}).collect(Collectors.toList());
//	    AggregatePatientRecord apr = new AggregatePatientRecord(p.patID.getValue(), p, history);
//	    LocalDate ld = new LocalDate();
//	    //ADChunk adc = src.munch(apr, ld);
////	    System.out.println(context);
//	    ApplicationContext context = new AnnotationConfigApplicationContext(Tester.class);
//	    SimpleRecordChewer container = (SimpleRecordChewer) context.getBean(SimpleRecordChewer.class);
//	    ADChunk adc = container.munch(apr, ld);
//	    System.out.println(adc);
//	    ObjectMapper objectMapper = new ObjectMapper();
//	    objectMapper.writeValue(new File("/Users/hnt5/data_extract/test.json"), adc);
//	}
//	
//	public static void main(String[] args) throws Exception {
//		File dir = com.google.common.io.Files.createTempDir();
//		ApplicationContext context = new AnnotationConfigApplicationContext(Tester.class);
//		InputStream patient = new FileInputStream(new File("/Users/hnt5/generated_data/patients.data"));
//		InputStream vaccines = new FileInputStream(new File("/Users/hnt5/generated_data/vaccines.data"));
//		ObjectComposer<AggregatePatientRecord> composer = (ObjectComposer<AggregatePatientRecord>) context.getBean(ObjectComposer.class);
//		LightWeightIndexer.Builder indexer = (LightWeightIndexer.Builder) context.getBean(LightWeightIndexer.Builder.class);
//		final int THREADS = 3;
//		ExecutorService executor = Executors.newFixedThreadPool(THREADS);
//		List<ChunkItemIterator<AggregatePatientRecord>> chunks;
//		SimpleRecordChewer container = (SimpleRecordChewer) context.getBean(SimpleRecordChewer.class);
//		MergeService merge = (MergeService) context.getBean(MergeService.class);
//		LocalDate date = new LocalDate();
//		
//		if (dir.exists()) {
//			String dirPath = Paths.get(dir.getAbsolutePath()).toString();
//
//			FileUtils.copyInputStreamToFile(patient, Paths.get(dir.getAbsolutePath(), "data",  EntityTypeRegistry.get("patient").i.file).toFile());
//			FileUtils.copyInputStreamToFile(vaccines, Paths.get(dir.getAbsolutePath(), "data",  EntityTypeRegistry.get("vaccination").i.file).toFile());
//			Paths.get(dir.getAbsolutePath(), "index").toFile().mkdirs();
//			
//			LightTextDB db = LightTextDB.refresh(dirPath, EntityTypeRegistry.get("patient"), indexer.build(), EntityTypeRegistry.get("vaccination"));
//			
//			
//			db.addComposer(AggregatePatientRecord.class, composer);
//			chunks = db.chunkIterator(AggregatePatientRecord.class, THREADS);
//			final ADChunk file = new ADChunk();
//			
//			for(ChunkItemIterator<AggregatePatientRecord> iterator : chunks){
//				Callable<Void> r = ()->{
//					try {
//						while(iterator.hasNext()){
//							AggregatePatientRecord apr = iterator.next();
//							merge.mergeChunk(file, container.munch(apr, date));
//							System.out.println(iterator.records());
//						}
//					}
//					catch(Exception e){
//						e.printStackTrace();
//					}
//					
//					return null;
//				};
//				executor.submit(r);
//				
//				
//			}
//			executor.shutdown();
//			executor.awaitTermination(2, TimeUnit.DAYS);
//			
//		    ObjectMapper objectMapper = new ObjectMapper();
//		    objectMapper.writeValue(new File("/Users/hnt5/generated_data/test.json"), file);
//		}
//	}
	

	
}
