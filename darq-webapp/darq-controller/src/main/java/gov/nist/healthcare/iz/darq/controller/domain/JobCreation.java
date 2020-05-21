package gov.nist.healthcare.iz.darq.controller.domain;

public class JobCreation {
	String name;
	String templateId;
	String adfId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
