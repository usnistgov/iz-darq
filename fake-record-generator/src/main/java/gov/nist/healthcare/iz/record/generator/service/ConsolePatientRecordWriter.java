package gov.nist.healthcare.iz.record.generator.service;

import gov.nist.healthcare.iz.record.generator.model.PatientRecord;

import java.util.Map;

public class ConsolePatientRecordWriter implements PatientRecordWriter {
    int i = 0;

    @Override
    public void write(PatientRecord record) {
        System.out.println("[Record ] "+ (i++) + " " + record.getId());
        System.out.println("* Patient : "+record.getPatient());
        int i = 0;
        for(Map<String, String> vx: record.getVaccinations()) {
            System.out.println("* Vaccination "+ (++i) +" : "+vx);
        }
    }

    @Override
    public void flush() {
        System.out.println("[FLUSHED]");
    }
}
