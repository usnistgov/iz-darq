package gov.nist.healthcare.iz.darq.analyzer.model.comparison;

import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.domain.Fraction;

import java.util.Map;

public class RowDifferential {
    private Map<Field, String> rowIdentifier;
    private boolean missingInNewReport;
    private boolean passStatusChanged;
    private Fraction baseResult;
    private Fraction newResult;

    public Map<Field, String> getRowIdentifier() {
        return rowIdentifier;
    }

    public void setRowIdentifier(Map<Field, String> rowIdentifier) {
        this.rowIdentifier = rowIdentifier;
    }

    public boolean isMissingInNewReport() {
        return missingInNewReport;
    }

    public void setMissingInNewReport(boolean missingInNewReport) {
        this.missingInNewReport = missingInNewReport;
    }

    public boolean isPassStatusChanged() {
        return passStatusChanged;
    }

    public void setPassStatusChanged(boolean passStatusChanged) {
        this.passStatusChanged = passStatusChanged;
    }

    public Fraction getBaseResult() {
        return baseResult;
    }

    public void setBaseResult(Fraction baseResult) {
        this.baseResult = baseResult;
    }

    public Fraction getNewResult() {
        return newResult;
    }

    public void setNewResult(Fraction newResult) {
        this.newResult = newResult;
    }
}