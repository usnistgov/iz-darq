package gov.nist.healthcare.iz.darq.model;

import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportTemplate;
import org.springframework.data.annotation.Id;

import java.util.Date;

public class AnalysisJob {

    @Id
    private String id;
    private String name;
    private String adfId;
    private String adfName;
    private ReportTemplate template;
    private Date submitTime;
    private Date startTime;
    private Date endTime;
    private JobStatus status;
    private String owner;
    private String failure;
    private String reportId;
    private String facilityId;

    public AnalysisJob() {
    }

    public AnalysisJob(String name, String adfId, String adfName, ReportTemplate template, String owner, String facilityId) {
        this.name = name;
        this.adfId = adfId;
        this.adfName = adfName;
        this.template = template;
        this.status = JobStatus.QUEUED;
        this.facilityId = facilityId;
        this.owner = owner;
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

    public ReportTemplate getTemplate() {
        return template;
    }

    public void setTemplate(ReportTemplate template) {
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

    public String getOwner() {
        return owner;
    }

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
}
