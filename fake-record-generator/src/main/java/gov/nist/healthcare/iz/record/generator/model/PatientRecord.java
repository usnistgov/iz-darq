package gov.nist.healthcare.iz.record.generator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientRecord {
    private String id;
    private Map<String, String> patient = new HashMap<>();
    private List<Map<String, String>> vaccinations = new ArrayList<>();

    public PatientRecord(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getPatient() {
        return patient;
    }

    public void setPatient(Map<String, String> patient) {
        this.patient = patient;
    }

    public List<Map<String, String>> getVaccinations() {
        return vaccinations;
    }

    public void setVaccinations(List<Map<String, String>> vaccinations) {
        this.vaccinations = vaccinations;
    }
}
