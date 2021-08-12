package gov.nist.healthcare.iz.record.generator.service;
import gov.nist.healthcare.iz.record.generator.RecordType;
import gov.nist.healthcare.iz.record.generator.model.Configuration;
import gov.nist.healthcare.iz.record.generator.model.PatientRecord;
import gov.nist.healthcare.iz.record.generator.model.RecordGenerationModel;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientRecordGenerator {
    Configuration configuration;
    RecordGenerationModel patient;
    RecordGenerationModel vaccination;
    int generated = 0;
    int vaccinations = 0;

    public PatientRecordGenerator(Configuration configuration) throws Exception {
        this.configuration = configuration;
    }

    public void generate(PatientRecordWriter writer) throws Exception {
        this.patient = RecordGenerationModelFactory.createRecordGenerationModel(RecordType.PATIENT, configuration, configuration.getPatients());
        this.vaccination = RecordGenerationModelFactory.createRecordGenerationModel(RecordType.VACCINATION, configuration, configuration.getVaccinations());
        this.vaccinations = configuration.getNumberOfVaccinations();
        System.out.println( this.patient.getFieldsGenerationOrder());

        for(int i = 0; i < configuration.getNumberOfPatients(); i++) {
            writer.write(generatePatientRecord(i == (configuration.getNumberOfPatients() - 1)));
            generated++;
        }
        writer.flush();
    }

    PatientRecord generatePatientRecord(boolean last) throws Exception {
        String ID = RandomStringUtils.randomAlphanumeric(5).toUpperCase();
        PatientRecord record = new PatientRecord(ID);
        populatePatient(record);
        populateVaccinations(record, last);
        return record;
    }

    Map<String, String> populatePatient(PatientRecord record) throws Exception {
        for(String field: patient.getFieldsGenerationOrder()) {
            patient.getGenerators().get(field).populate(record, RecordType.PATIENT, field, record.getPatient());
        }
        return record.getPatient();
    }

    List<Map<String, String>> populateVaccinations(PatientRecord record, boolean last) throws Exception {
        int randomNumberOfVaccinations = RandomUtils.nextInt(configuration.getMinVxPerPatient(), configuration.getMaxVxPerPatient());
        int number = last || ((this.vaccinations - randomNumberOfVaccinations) < 0)? this.vaccinations : randomNumberOfVaccinations;
        for(int i = 0; i < number; i++) {
            this.populateVaccination(record);
        }
        this.vaccinations -= number;
        return record.getVaccinations();
    }

    Map<String, String> populateVaccination(PatientRecord record) throws Exception {
        Map<String, String> vx = new HashMap<>();
        for(String field: vaccination.getFieldsGenerationOrder()) {
            vaccination.getGenerators().get(field).populate(record, RecordType.VACCINATION, field, vx);
        }
        record.getVaccinations().add(vx);
        return vx;
    }

}
