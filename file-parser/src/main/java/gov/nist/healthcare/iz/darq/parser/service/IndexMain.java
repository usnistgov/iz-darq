package gov.nist.healthcare.iz.darq.parser.service;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;
import gov.nist.healthcare.iz.darq.parser.model.Patient;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;
import gov.nist.lightdb.domain.EntityTypeRegistry;
import gov.nist.lightdb.domain.EntityTypeRegistry.EntityType;
import gov.nist.lightdb.service.LightTextDB;
import gov.nist.lightdb.service.LightWeightIndexer;
import gov.nist.lightdb.service.ObjectComposer;
import gov.nist.lightdb.domain.Source;
import gov.nist.lightdb.domain.Time;

public class IndexMain {

	public static void main(String[] args) throws Exception {
		
		EntityType pType = EntityTypeRegistry.register("patient", "patients.data", "p", Patient.class);
		EntityType vType = EntityTypeRegistry.register("vaccination", "vaccines.data", "v", VaccineRecord.class);
		
		LightWeightIndexer indexer = LightWeightIndexer.builder()
		.document((ID) ->{
			return ID.until('\t');
		})
		.commit(250000)
		.chunks(6)
		.build();
		
		ObjectComposer<AggregatePatientRecord> aggPrComposer = ObjectComposer.<AggregatePatientRecord> builder()
		.<Patient> parser(pType, new PatientParser())
		.<VaccineRecord> parser(vType, new VaccinationParser())
		.composer((ID, pieces) -> {
			List<Patient> patients = pieces.get(pType);
			List<VaccineRecord> history = pieces.get(vType);
			
			return new AggregatePatientRecord(ID, patients.get(0), history);
		})
		.build();
		
		Source master = new Source(pType, Paths.get("/Users/hnt5/generated_data/patients.data"));
		List<Source> slaves = Arrays.asList(new Source(vType, Paths.get("/Users/hnt5/generated_data/vaccines.data")));
		
		Time.INSTANCE.init();
		LightTextDB db = LightTextDB
						.builder()
						.use("/Users/hnt5/light-text-db")
						.slaves(slaves)
						.indexer(indexer)
						.openWithMaster(master);
		Time.INSTANCE.checkPoint("[INDEXED]");

	}
}
