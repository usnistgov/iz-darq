package gov.nist.healthcare.iz.darq.service.domain;

public class AnalysisJobCreateData extends CreateJobData {
	private String templateId;
	private String adfId;

	public AnalysisJobCreateData(String name, String ownerId, String templateId, String adfId) {
		super(name, ownerId);
		this.templateId = templateId;
		this.adfId = adfId;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getAdfId() {
		return adfId;
	}

	public void setAdfId(String adfId) {
		this.adfId = adfId;
	}
}
