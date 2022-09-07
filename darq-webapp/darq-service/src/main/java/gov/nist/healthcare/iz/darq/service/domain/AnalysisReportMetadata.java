package gov.nist.healthcare.iz.darq.service.domain;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisReport;

public class AnalysisReportMetadata {
    protected String id;
    protected String ownerId;
    protected String templateId;
    protected boolean published;

    public AnalysisReportMetadata(AnalysisReport report) {
        this.id = report.getId();
        this.ownerId = report.getOwnerId();
        this.templateId = report.getReportTemplate().getId();
        this.published = report.isPublished();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}
