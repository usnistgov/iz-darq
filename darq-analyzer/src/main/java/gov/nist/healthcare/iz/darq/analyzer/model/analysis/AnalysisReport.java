package gov.nist.healthcare.iz.darq.analyzer.model.analysis;

import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportTemplate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document
public class AnalysisReport extends Report {

    private String templateId;
    List<ReportSectionResult> sections;

    public void fromTemplate(ReportTemplate template) {
        this.setConfiguration(template.getConfiguration());
        this.setDescription(template.getDescription());
        this.setLastUpdated(new Date());
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public List<ReportSectionResult> getSections() {
        return sections;
    }

    public void setSections(List<ReportSectionResult> sections) {
        this.sections = sections;
    }
}
