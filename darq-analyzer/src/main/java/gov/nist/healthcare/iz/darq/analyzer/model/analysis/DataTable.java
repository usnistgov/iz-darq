package gov.nist.healthcare.iz.darq.analyzer.model.analysis;
import gov.nist.healthcare.iz.darq.analyzer.model.template.QueryPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Field;

import java.util.*;
import java.util.stream.Collectors;

public class DataTable {
    QueryPayload query;
    QueryIssues issues;
    boolean thresholdViolation;
    Set<Field> nominator;
    Set<Field> denominator;
    List<DataTableRow> values;

    public void fromQuery(QueryPayload query) {
        this.query = query;
        this.denominator = query.getDenominatorFields();
        this.nominator = query.getNominatorFields().stream().filter((v) -> !this.denominator.contains(v)).collect(Collectors.toSet());
        this.values = new ArrayList<>();
    }

    public QueryIssues getIssues() {
        return issues;
    }

    public void setIssues(QueryIssues issues) {
        this.issues = issues;
    }

    public List<DataTableRow> getValues() {
        return values;
    }

    public boolean isThresholdViolation() {
        return thresholdViolation;
    }

    public void setThresholdViolation(boolean thresholdViolation) {
        this.thresholdViolation = thresholdViolation;
    }

    public void addThreshold(boolean b) {
        if(!b) {
            this.setThresholdViolation(true);
        }
    }

    public QueryPayload getQuery() {
        return query;
    }

    public void setQuery(QueryPayload query) {
        this.query = query;
    }

    public Set<Field> getNominator() {
        return nominator;
    }

    public void setNominator(Set<Field> nominator) {
        this.nominator = nominator;
    }

    public Set<Field> getDenominator() {
        return denominator;
    }

    public void setDenominator(Set<Field> denominator) {
        this.denominator = denominator;
    }

    public void setValues(List<DataTableRow> values) {
        this.values = values;
    }
}
