package gov.nist.healthcare.iz.darq.analyzer.model.comparison;

import java.util.ArrayList;
import java.util.List;

public class SectionDifferential {
    private String sectionId;
    private boolean missingInNewReport;
    private boolean thresholdViolationChanged;
    private List<TableDifferential> tableDifferentials = new ArrayList<>();
    private List<SectionDifferential> children = new ArrayList<>();

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public boolean isMissingInNewReport() {
        return missingInNewReport;
    }

    public void setMissingInNewReport(boolean missingInNewReport) {
        this.missingInNewReport = missingInNewReport;
    }

    public boolean isThresholdViolationChanged() {
        return thresholdViolationChanged;
    }

    public void setThresholdViolationChanged(boolean thresholdViolationChanged) {
        this.thresholdViolationChanged = thresholdViolationChanged;
    }

    public List<TableDifferential> getTableDifferentials() {
        return tableDifferentials;
    }

    public void setTableDifferentials(List<TableDifferential> tableDifferentials) {
        this.tableDifferentials = tableDifferentials;
    }

    public List<SectionDifferential> getChildren() {
        return children;
    }

    public void setChildren(List<SectionDifferential> children) {
        this.children = children;
    }
}