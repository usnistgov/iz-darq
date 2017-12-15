package gov.nist.healthcare.iz.darq.parser.service;


import java.util.List;

import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;
import gov.nist.healthcare.iz.darq.parser.model.Patient;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;
import gov.nist.lightdb.domain.EntityTypeRegistry;
import gov.nist.lightdb.domain.EntityTypeRegistry.EntityType;
import gov.nist.lightdb.service.LightTextDB;
import gov.nist.lightdb.service.LightWeigthSearcher.ChunkItemIterator;
import gov.nist.lightdb.service.ObjectComposer;
import gov.nist.lightdb.domain.Time;

public class SearcherMain {

	public static void main(String[] args) throws Exception {
		
		EntityType pType = EntityTypeRegistry.register("patient", "patients.data", "p", Patient.class);
		EntityType vType = EntityTypeRegistry.register("vaccination", "vaccines.data", "v", VaccineRecord.class);
		
		ObjectComposer<AggregatePatientRecord> aggPrComposer = ObjectComposer.<AggregatePatientRecord> builder()
		.<Patient> parser(pType, new PatientParser())
		.<VaccineRecord> parser(vType, new VaccinationParser())
		.composer((ID, pieces) -> {
			List<Patient> patients = pieces.get(pType);
			List<VaccineRecord> history = pieces.get(vType);
			
			return new AggregatePatientRecord(ID, patients.get(0), history);
		})
		.build();
		
		
		Time.INSTANCE.init();
		LightTextDB db = LightTextDB.open("/Users/hnt5/light-text-db");
		db.addComposer(AggregatePatientRecord.class, aggPrComposer);
		
		List<ChunkItemIterator<AggregatePatientRecord>> chunks = db.chunkIterator(AggregatePatientRecord.class, 1001);
		
		Time.INSTANCE.checkPoint("chunks "+chunks.size());
		
		ChunkItemIterator<AggregatePatientRecord> first = chunks.get(0);
		ChunkItemIterator<AggregatePatientRecord> last = chunks.get(chunks.size()-1);
		
		System.out.println(first.hasNext());
		System.out.println(last.hasNext());
		
		int i = 0;
		while(first.hasNext()){
			first.next();
			i++;
		}
		int j = 0;
		while(last.hasNext()){
			last.next();
			j++;
		}
		System.out.println(" I "+i+" J "+j);
		
		Time.INSTANCE.checkPoint("[INDEXED]");

	}
}
