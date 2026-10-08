package gov.nist.healthcare.iz.darq.analyzer.model.comparison;

import java.util.ArrayList;
import java.util.List;

public class TableDifferential {
    private int tableIndex;
    private String tableCaption;
    private boolean missingInNewReport;
    private boolean thresholdViolationChanged;
    private List<RowDifferential> rowDifferentials = new ArrayList<>();

    public int getTableIndex() {
        return tableIndex;
    }

    public void setTableIndex(int tableIndex) {
        this.tableIndex = tableIndex;
    }

    public String getTableCaption() {
        return tableCaption;
    }

    public void setTableCaption(String tableCaption) {
        this.tableCaption = tableCaption;
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

    public List<RowDifferential> getRowDifferentials() {
        return rowDifferentials;
    }

    public void setRowDifferentials(List<RowDifferential> rowDifferentials) {
        this.rowDifferentials = rowDifferentials;
    }
}