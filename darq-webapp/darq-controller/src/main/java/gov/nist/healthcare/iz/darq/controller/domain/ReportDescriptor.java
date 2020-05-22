package gov.nist.healthcare.iz.darq.controller.domain;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisReport;
import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportTemplate;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.model.ConfigurationDescriptor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.Date;

public class ReportDescriptor {
    @Id
    protected String id;
    protected String name;
    protected String description;
    protected TemplateDescriptor template;
    protected String owner;
    @Transient
    protected boolean viewOnly;
    protected boolean published;
    protected Date lastUpdated;
    private String facilityId;
    private String adfName;
    private Date publishDate;

    public ReportDescriptor() {
    }

    public ReportDescriptor(AnalysisReport report) {
        this.id = report.getId();
        this.name = report.getName();
        this.description = report.getDescription();
        this.template = new TemplateDescriptor(
                report.getReportTemplate().getId(),
                report.getReportTemplate().getName(),
                report.getReportTemplate().getOwner(),
                null,
                report.getReportTemplate().getOwner().equals(report.getOwner()),
                report.getReportTemplate().isPublished(),
                !report.getReportTemplate().getOwner().equals(report.getOwner()) && !report.getReportTemplate().isPublished()
        );
        this.adfName = report.getAdfName();
        this.publishDate = report.getPublishDate();
        this.owner = report.getOwner();
        this.viewOnly = report.isViewOnly();
        this.lastUpdated = report.getLastUpdated();
        this.facilityId = report.getFacilityId();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isViewOnly() {
        return viewOnly;
    }

    public void setViewOnly(boolean viewOnly) {
        this.viewOnly = viewOnly;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public TemplateDescriptor getTemplate() {
        return template;
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

    public void setTemplate(TemplateDescriptor template) {
        this.template = template;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }
}
