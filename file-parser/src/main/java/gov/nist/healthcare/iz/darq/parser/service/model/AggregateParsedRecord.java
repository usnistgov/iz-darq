package gov.nist.healthcare.iz.darq.parser.service.model;

import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;
import gov.nist.healthcare.iz.darq.parser.model.Patient;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AggregateParsedRecord {

    private ParsedRecord<Patient> patient;
    private List<ParsedRecord<VaccineRecord>> vaccinations;
    private AggregatePatientRecord apr;
    private boolean valid;
    private List<ParseError> issues;
    private int skippedPatient;
    private int skippedVaccination;

    public AggregateParsedRecord(ParsedRecord<Patient> patient, List<ParsedRecord<VaccineRecord>> vaccinations) {
        this.patient = patient;
        this.vaccinations = vaccinations;
        this.issues = new ArrayList<>();

        this.issues.addAll(
                patient
                        .getIssues()
                        .stream()
                        .map(issue ->
                                new ParseError(patient.getID(), issue.getField(), issue.getRecord(), issue.getMessage(), issue.isCritical(), patient.getLine())
                        )
                        .collect(Collectors.toList())
        );

        this.issues.addAll(
                vaccinations
                        .stream()
                        .map(vax ->
                                 vax.getIssues()
                                    .stream()
                                    .map(issue ->
                                        new ParseError(vax.getID(), issue.getField(), issue.getRecord(), issue.getMessage(), issue.isCritical(), vax.getLine())
                                    )
                                    .collect(Collectors.toList())
                        )
                        .flatMap(errors -> errors.stream())
                        .collect(Collectors.toList())
        );

        // Check if vaccination ID are UNIQUE
        List<String> ids = new ArrayList<>();
        List<VaccineRecord> vaccineRecords = this.vaccinations.stream()
                .filter(vax -> !vax.hasCriticalIssue())
                .filter(vr -> {
                    if(ids.contains(vr.getRecord().vax_event_id.getValue())) {
//                        String field, String record, String message, boolean critical, int line
                        issues.add(new ParseError(vr.getID(), "VACCINATION_EVENT_ID", "VACCINATION_RECORD", "Duplicate ID "+vr.getRecord().vax_event_id.getValue(), true, vr.getLine()));
                        return false;
                    } else {
                        ids.add(vr.getRecord().vax_event_id.getValue());
                        return true;
                    }
                })
                .map(ParsedRecord<VaccineRecord>::getRecord)
                .collect(Collectors.toList());

        if(patient.hasCriticalIssue()) {
            this.skippedPatient = 1;
            this.skippedVaccination = vaccinations.size();
            this.valid = false;
        } else {
            this.skippedPatient = 0;
            this.skippedVaccination = (int) vaccinations.size() - vaccineRecords.size();
            this.valid = true;
            this.apr = new AggregatePatientRecord(patient.getID(), patient.getRecord(), vaccineRecords);

        }
    }

    public ParsedRecord<Patient> getPatient() {
        return patient;
    }

    public void setPatient(ParsedRecord<Patient> patient) {
        this.patient = patient;
    }

    public List<ParsedRecord<VaccineRecord>> getVaccinations() {
        return vaccinations;
    }

    public void setVaccinations(List<ParsedRecord<VaccineRecord>> vaccinations) {
        this.vaccinations = vaccinations;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<ParseError> getIssues() {
        return issues;
    }

    public void setIssues(List<ParseError> issues) {
        this.issues = issues;
    }

    public int getSkippedPatient() {
        return skippedPatient;
    }

    public int getSkippedVaccination() {
        return skippedVaccination;
    }

    public AggregatePatientRecord getApr() {
        return apr;
    }
}
