package gov.nist.healthcare.iz.darq.batch.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;
import gov.nist.healthcare.iz.darq.parser.model.Patient;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;
import gov.nist.healthcare.iz.darq.parser.service.PatientParser;
import gov.nist.healthcare.iz.darq.parser.service.VaccinationParser;
import gov.nist.lightdb.domain.EntityTypeRegistry;
import gov.nist.lightdb.domain.EntityTypeRegistry.EntityType;
import gov.nist.lightdb.service.LightWeightIndexer;
import gov.nist.lightdb.service.ObjectComposer;

@Configuration
public class LightTextDBConfiguration {
	
    @Bean
    public EntityType pType(){
    	return EntityTypeRegistry.register("patient", "patients.data", "p", Patient.class);
    }
    
    @Bean
    public EntityType vType(){
    	return EntityTypeRegistry.register("vaccination", "vaccines.data", "v", VaccineRecord.class);
    }
    
    @Bean
    public ObjectComposer<AggregatePatientRecord> composer(){
		
    	return ObjectComposer.<AggregatePatientRecord> builder()
		.<Patient> parser(pType(), new PatientParser())
		.<VaccineRecord> parser(vType(), new VaccinationParser())
		.composer((ID, pieces) -> {
			List<Patient> patients = pieces.get(pType());
			List<VaccineRecord> history = pieces.get(vType());
			
			return new AggregatePatientRecord(ID, patients.get(0), history);
		})
		.build();
    }
    
    @Bean
    public LightWeightIndexer.Builder indexer(){
		return LightWeightIndexer.builder()
		.document((ID) ->{
			return ID.until('\t');
		})
		.commit(250000);
    }
}
