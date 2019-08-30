package gov.nist.healthcare.iz.darq.digest.service.exception;

import gov.nist.healthcare.iz.darq.parser.service.model.ParseError;

import java.util.List;

public class InvalidPatientRecord extends Exception {
    private List<ParseError> issues;

    public InvalidPatientRecord(List<ParseError> issues) {
        this.issues = issues;
    }

    public List<ParseError> getIssues() {
        return issues;
    }
}