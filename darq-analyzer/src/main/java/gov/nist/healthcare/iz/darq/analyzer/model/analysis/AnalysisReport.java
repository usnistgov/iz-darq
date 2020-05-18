package gov.nist.healthcare.iz.darq.analyzer.model.analysis;

import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportTemplate;

import java.util.List;

public class AnalysisReport extends ReportTemplate {

    private String templateId;
    List<ReportSectionResult> sections;

    @Override
    public List<ReportSectionResult> getSections() {
        return sections;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

}
