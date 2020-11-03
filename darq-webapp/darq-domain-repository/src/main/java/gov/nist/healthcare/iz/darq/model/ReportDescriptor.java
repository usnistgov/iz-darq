package gov.nist.healthcare.iz.darq.controller.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nist.healthcare.domain.trait.Owned;
import gov.nist.healthcare.domain.trait.Publishable;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisReport;
import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportTemplate;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.model.ConfigurationDescriptor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.Date;

public class ReportDescriptor implements Owned, Publishable {
    @Id
    protected String id;
    protected String name;
    protected String description;
    protected TemplateDescriptor template;
    @Deprecated
    protected String owner;
    protected String ownerId;
    protected String ownerDisplayName;
    protected boolean published;
    protected Date lastUpdated;
    private String facilityId;
    private String adfName;
    private Date publishDate;

    public ReportDescriptor() {
    }

    public ReportDescriptor(AnalysisReport report, String ownerDisplayName, TemplateDescriptor template) {
        this.id = report.getId();
        this.name = report.getName();
        this.description = report.getDescription();
        this.template = template;
        this.ownerDisplayName = ownerDisplayName;
        this.adfName = report.getAdfName();
        this.publishDate = report.getPublishDate();
        this.owner = report.getOwner();
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

    @Deprecated
    @JsonIgnore
    public String getOwner() {
        return owner;
    }
    @Deprecated
    public void setOwner(String owner) {
        this.owner = owner;
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

    public String getOwnerDisplayName() {
        return ownerDisplayName;
    }

    public void setOwnerDisplayName(String ownerDisplayName) {
        this.ownerDisplayName = ownerDisplayName;
    }

    @Override
    @JsonProperty("owner")
    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    @Transient
    @JsonProperty("public")
    public boolean isPublic() {
        return this.isPublished();
    }
}
