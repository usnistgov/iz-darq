package gov.nist.healthcare.iz.darq.analyzer.model.analysis;

import gov.nist.healthcare.domain.trait.AssignableToFacility;
import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportTemplate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document
public class AnalysisReport extends Report implements AssignableToFacility {

    private ReportTemplate reportTemplate;
    private String facilityId;
    private String adfName;
    private Date publishDate;
    List<ReportSectionResult> sections;

    public void fromTemplate(ReportTemplate template) {
        this.setConfiguration(template.getConfiguration());
        this.setDescription(template.getDescription());
        this.setLastUpdated(new Date());
    }

    public String getAdfName() {
        return adfName;
    }

    public void setAdfName(String adfName) {
        this.adfName = adfName;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public ReportTemplate getReportTemplate() {
        return reportTemplate;
    }

    public void setReportTemplate(ReportTemplate reportTemplate) {
        this.reportTemplate = reportTemplate;
    }

    public List<ReportSectionResult> getSections() {
        return sections;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public void setSections(List<ReportSectionResult> sections) {
        this.sections = sections;
    }
}
