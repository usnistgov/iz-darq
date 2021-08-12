package gov.nist.healthcare.iz.record.generator.model;

import java.util.Map;

public class DetectionInject {
    Map<String, String> patient;
    Map<String, String> vaccination;

    public Map<String, String> getPatient() {
        return patient;
    }

    public void setPatient(Map<String, String> patient) {
        this.patient = patient;
    }

    public Map<String, String> getVaccination() {
        return vaccination;
    }

    public void setVaccination(Map<String, String> vaccination) {
        this.vaccination = vaccination;
    }
}
