package gov.nist.healthcare.iz.record.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nist.healthcare.iz.record.generator.model.Configuration;
import gov.nist.healthcare.iz.record.generator.service.ConsolePatientRecordWriter;
import gov.nist.healthcare.iz.record.generator.service.PatientRecordGenerator;

import java.io.InputStream;

public class Test {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream configurationIs = Test.class.getResourceAsStream("/configuration.json");
        Configuration configuration = mapper.readValue(configurationIs, Configuration.class);
//        Configuration configuration = new Configuration();
//        configuration.setAsOf(LocalDate.now());
//        configuration.setNumberOfPatients(100);
//        configuration.setNumberOfVaccinations(200);
//        configuration.setMaxVxPerPatient(5);
//        configuration.setMinVxPerPatient(0);
//
//        Map<String, Field> patient = new HashMap<>();
//        patient.put("NAME", new RandomField("NAME", 3, 10));
//        Map<String, Integer> sex = new HashMap<>();
//        sex.put("M", 60);
//        sex.put("F", 40);
//        patient.put("GENDER", new DistributedCodedField("GENDER", CodesetType.PATIENT_SEX, sex));
//        configuration.setPatients(patient);
//
//        Map<String, Field> vaccine = new HashMap<>();
//        vaccine.put("CVX", new DistributedCodedField("CVX", CodesetType.VACCINATION_CVX_CODE, null));
//        vaccine.put("MVX", new RelatedCodeField("MVX", CodesetType.VACCINATION_MANUFACTURER_CODE, "CVX"));
//        configuration.setVaccinations(vaccine);

        ConsolePatientRecordWriter writer = new ConsolePatientRecordWriter();
        PatientRecordGenerator generator = new PatientRecordGenerator(configuration);
        generator.generate(writer);
    }
}
