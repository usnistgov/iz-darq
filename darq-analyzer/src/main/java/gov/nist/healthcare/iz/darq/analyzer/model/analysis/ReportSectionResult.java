package gov.nist.healthcare.iz.darq.analyzer.model.analysis;
import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportSection;

import java.util.ArrayList;
import java.util.List;

public class ReportSectionResult extends Section {
    boolean thresholdViolation;
    List<DataTable> data;
    List<ReportSectionResult> children;
    String comment;

    public void fromSection(ReportSection sectionTemplate) {
        this.setId(sectionTemplate.getId());
        this.setPath(sectionTemplate.getPath());
        this.setPosition(sectionTemplate.getPosition());
        this.setHeader(sectionTemplate.getHeader());
        this.setText(sectionTemplate.getText());
        data = new ArrayList<>();
    }

    public List<DataTable> getData() {
        return data;
    }

    public void setData(List<DataTable> data) {
        this.data = data;
    }

    public List<ReportSectionResult> getChildren() {
        return children;
    }

    public void setChildren(List<ReportSectionResult> children) {
        this.children = children;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isThresholdViolation() {
        return thresholdViolation;
    }

    public void setThresholdViolation(boolean thresholdViolation) {
        this.thresholdViolation = thresholdViolation;
    }
}
