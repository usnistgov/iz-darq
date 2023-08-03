package gov.nist.healthcare.iz.darq.analyzer.model.analysis;

import gov.nist.healthcare.iz.darq.analyzer.model.template.Threshold;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.domain.Fraction;

import java.util.Map;
import java.util.Objects;

public class DataTableRow {
    int groupId;
    Map<Field, String> values;
    Fraction result;
    AdjustedFraction adjustedFraction;
    Threshold threshold;
    boolean pass = true;

    public DataTableRow(Map<Field, String> values, Fraction result, Threshold threshold, boolean pass) {
        this.values = values;
        this.result = result;
        this.threshold = threshold;
        this.pass = pass;
    }

    public DataTableRow() {
    }

    public Map<Field, String> getValues() {
        return values;
    }

    public void setValues(Map<Field, String> values) {
        this.values = values;
    }

    public Fraction getEffectiveResult() {
        return adjustedFraction != null ? adjustedFraction : result;
    }

    public Fraction getResult() {
       return result;
    }

    public void setResult(Fraction result) {
        this.result = result;
    }

    public Threshold getThreshold() {
        return threshold;
    }

    public void setThreshold(Threshold threshold) {
        this.threshold = threshold;
    }

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public AdjustedFraction getAdjustedFraction() {
        return adjustedFraction;
    }

    public void setAdjustedFraction(AdjustedFraction adjustedFraction) {
        this.adjustedFraction = adjustedFraction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataTableRow that = (DataTableRow) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
