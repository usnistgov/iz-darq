package gov.nist.healthcare.iz.darq.model;

import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportTemplate;

public class AnalysisJob extends Job {

    private String adfId;
    private String adfName;
    private ReportTemplate template;
    private String reportId;

    public AnalysisJob() {
    }

    public AnalysisJob(String name, String adfId, String adfName, ReportTemplate template, String owner, String ownerId, String facilityId) {
        this.setName(name);
        this.adfId = adfId;
        this.adfName = adfName;
        this.template = template;
        this.setStatus(JobStatus.QUEUED);
        this.setFacilityId(facilityId);
        this.setOwner(owner);
        this.setOwnerId(ownerId);
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

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getAdfName() {
        return adfName;
    }

    public void setAdfName(String adfName) {
        this.adfName = adfName;
    }

}
