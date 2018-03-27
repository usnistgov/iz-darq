package gov.nist.healthcare.iz.darq.analyzer.domain;

import java.util.List;

public class ReportSection {
	
	private String title;
	private String description;
	private List<AnalysisPayload> payloads; 
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<AnalysisPayload> getPayloads() {
		return payloads;
	}
	public void setPayloads(List<AnalysisPayload> payloads) {
		this.payloads = payloads;
	}

}
