package gov.nist.healthcare.iz.darq.parser.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;
import gov.nist.healthcare.iz.darq.parser.model.Patient;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;
import gov.nist.lightdb.domain.EntityTypeRegistry;
import gov.nist.lightdb.domain.EntityTypeRegistry.EntityType;
import gov.nist.lightdb.domain.Time;
import gov.nist.lightdb.service.LightTextDB;
import gov.nist.lightdb.service.LightWeightIndexer;
import gov.nist.lightdb.service.LightWeigthSearcher.ItemIterator;
import gov.nist.lightdb.service.ObjectComposer;

public class Mn {

	public static class A {
		public String p;

		public A(String p) {
			super();
			this.p = p;
		}

		@Override
		public String toString() {
			return "A [p=" + p + "]";
		}
		
		public static A flush(A x){
			A inter = x;
			x = null;
			return inter;
		}
		
	}
	
	
	public static class B {
		
		public A a = new A("x");
		
		public A flush(){
			A inter = a;
			a = new A("y");
			return inter;
		}
	}
	
	public static void main(String[] args) throws IOException {
		
//		B a = new B();
//		
////		System.out.println(a.a);
//		System.out.println(a.flush());
//		System.out.println(a.a);
		
//		EntityType pType = EntityTypeRegistry.register("patient", "patients.data", "p", Patient.class);
//		EntityType vType = EntityTypeRegistry.register("vaccination", "vaccines.data", "v", VaccineRecord.class);
		
//	
		
//		LightWeightIndexer indexer = LightWeightIndexer.builder()
//		.document((ID) ->{
//			return ID.until('\t');
//		})
//		.commit(250000)
//		.build();
		Time.INSTANCE.init();
		long lineCount = Files.lines(Paths.get("/Users/hnt5/generated_data/patients.data")).count();
		Time.INSTANCE.checkPoint(" COUNT "+lineCount);
//	
//		
//		ObjectComposer<AggregatePatientRecord> aggPrComposer = ObjectComposer.<AggregatePatientRecord> builder()
//		.<Patient> parser(pType, new PatientParser())
//		.<VaccineRecord> parser(vType, new VaccinationParser())
//		.composer((ID, pieces) -> {
//			List<Patient> patients = pieces.get(pType);
//			List<VaccineRecord> history = pieces.get(vType);
//			
//			return new AggregatePatientRecord(ID, patients.get(0), history);
//		})
//		.build();
//
//		
//		SourceByPath master = new SourceByPath(pType, Paths.get("/Users/hnt5/generated_data/patients.data"));
//		List<SourceByPath> slaves = Arrays.asList(new SourceByPath(vType, Paths.get("/Users/hnt5/generated_data/vaccines.data")));
		
//		try {
//			
//			Time.INSTANCE.init();
//			//LightTextDB db = LightTextDB.create_p(Paths.get("/Users/hnt5/light-text-db"), master, slaves, indexer);
//			LightTextDB db = LightTextDB.open(Paths.get("/Users/hnt5/light-text-db").toString());
//			db.addComposer(AggregatePatientRecord.class, aggPrComposer);
//			Time.INSTANCE.checkPoint("CREATED");
//			
//			ItemIterator<AggregatePatientRecord> iter = db.iterator(AggregatePatientRecord.class);
//			
//			Runnable r = () -> {
//				while(iter.hasNext()){
//					iter.next();
//					System.out.println(iter.records());
//				}
//			};
//			
//			Thread t1 = new Thread(r);
//			Thread t2 = new Thread(r);
//			Thread t3 = new Thread(r);
//			
//			t1.start();
//			t2.start();
//			t3.start();
			
//			System.out.println(iter.hasNext());
//			System.out.println(iter.next());
//			Time.INSTANCE.checkPoint("FIRST");
//			iter.close();
//			Runtime.getRuntime().gc();
//			Thread.sleep(10000);
			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
