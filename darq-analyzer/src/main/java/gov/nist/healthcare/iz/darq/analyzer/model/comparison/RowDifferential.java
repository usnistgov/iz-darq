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
    private String thresholdText;
    private boolean basePass;
    private boolean newPass;
    private Double basePercent;
    private Double newPercent;
    private Double minPercent;
    private Double meanPercent;
    private Double maxPercent;
    private Double percentOfReportsPassing;


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

    public String getThresholdText() {
        return thresholdText;
    }

    public void setThresholdText(String thresholdText) {
        this.thresholdText = thresholdText;
    }

    public boolean isBasePass() {
        return basePass;
    }

    public void setBasePass(boolean basePass) {
        this.basePass = basePass;
    }

    public boolean isNewPass() {
        return newPass;
    }

    public void setNewPass(boolean newPass) {
        this.newPass = newPass;
    }

    public Double getBasePercent() {
        return basePercent;
    }

    public void setBasePercent(Double basePercent) {
        this.basePercent = basePercent;
    }

    public Double getNewPercent() {
        return newPercent;
    }

    public void setNewPercent(Double newPercent) {
        this.newPercent = newPercent;
    }

    public Double getMinPercent() {
        return minPercent;
    }

    public void setMinPercent(Double minPercent) {
        this.minPercent = minPercent;
    }

    public Double getMeanPercent() {
        return meanPercent;
    }

    public void setMeanPercent(Double meanPercent) {
        this.meanPercent = meanPercent;
    }

    public Double getMaxPercent() {
        return maxPercent;
    }

    public void setMaxPercent(Double maxPercent) {
        this.maxPercent = maxPercent;
    }

    public Double getPercentOfReportsPassing() {
        return percentOfReportsPassing;
    }

    public void setPercentOfReportsPassing(Double percentOfReportsPassing) {
        this.percentOfReportsPassing = percentOfReportsPassing;
    }
}