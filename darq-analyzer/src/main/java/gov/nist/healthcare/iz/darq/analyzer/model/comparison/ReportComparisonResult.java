package gov.nist.healthcare.iz.darq.analyzer.model.comparison;

import java.util.ArrayList;
import java.util.List;

public class ReportComparisonResult {
    private String baseReportId;
    private String newReportId;
    private String templateId;
    private List<SectionDifferential> sectionDifferentials = new ArrayList<>();

    public String getBaseReportId() {
        return baseReportId;
    }

    public void setBaseReportId(String baseReportId) {
        this.baseReportId = baseReportId;
    }

    public String getNewReportId() {
        return newReportId;
    }

    public void setNewReportId(String newReportId) {
        this.newReportId = newReportId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public List<SectionDifferential> getSectionDifferentials() {
        return sectionDifferentials;
    }

    public void setSectionDifferentials(List<SectionDifferential> sectionDifferentials) {
        this.sectionDifferentials = sectionDifferentials;
    }
}