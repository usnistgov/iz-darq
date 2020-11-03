package gov.nist.healthcare.iz.darq.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nist.healthcare.domain.trait.AssignableToFacility;
import gov.nist.healthcare.domain.trait.Owned;
import org.springframework.data.annotation.Id;

import java.util.Date;

public class AnalysisJobDescriptor implements Owned, AssignableToFacility {

    @Id
    private String id;
    private String name;
    private String adfId;
    private String adfName;
    private TemplateDescriptor template;
    private Date submitTime;
    private Date startTime;
    private Date endTime;
    private JobStatus status;
    @Deprecated
    private String owner;
    private String ownerId;
    private String ownerDisplayName;
    private String failure;
    private String reportId;
    private String facilityId;

    public AnalysisJobDescriptor(String id, String name, String adfId, String adfName, TemplateDescriptor template, Date submitTime, Date startTime, Date endTime, JobStatus status, String owner, String ownerId, String ownerDisplayName, String failure, String reportId, String facilityId) {
        this.id = id;
        this.name = name;
        this.adfId = adfId;
        this.adfName = adfName;
        this.template = template;
        this.submitTime = submitTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.owner = owner;
        this.ownerId = ownerId;
        this.ownerDisplayName = ownerDisplayName;
        this.failure = failure;
        this.reportId = reportId;
        this.facilityId = facilityId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdfId() {
        return adfId;
    }

    public void setAdfId(String adfId) {
        this.adfId = adfId;
    }

    public TemplateDescriptor getTemplate() {
        return template;
    }

    public void setTemplate(TemplateDescriptor template) {
        this.template = template;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public String getFailure() {
        return failure;
    }

    public void setFailure(String failure) {
        this.failure = failure;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
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

    public String getAdfName() {
        return adfName;
    }

    public void setAdfName(String adfName) {
        this.adfName = adfName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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
}
