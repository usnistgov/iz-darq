package gov.nist.healthcare.iz.darq.parser.service.model;

import gov.nist.healthcare.iz.darq.parser.model.Issue;

import java.util.List;

public class ParsedRecord<T extends Record> extends Record {

    private T record;
    private int line;
    private List<Issue> issues;

    public T getRecord() {
        return record;
    }

    public void setRecord(T record) {
        this.record = record;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

    public boolean hasCriticalIssue() {
        return this.issues.stream().filter(issue -> issue.isCritical()).findFirst().isPresent();
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    @Override
    public String getID() {
        return record != null ? record.getID() : null;
    }
}
