package gov.nist.healthcare.iz.darq.adf.model;

import java.util.Date;
import java.util.Set;

public class Metadata {
	private String version;
	private String build;
	private String mqeVersion;
	private long totalAnalysisTime;
	private Date analysisDate;
	private Set<String> inactiveDetections;

	public Metadata(String version, String build, String mqeVersion, long totalAnalysisTime, Date analysisDate, Set<String> inactiveDetections) {
		this.version = version;
		this.build = build;
		this.mqeVersion = mqeVersion;
		this.totalAnalysisTime = totalAnalysisTime;
		this.analysisDate = analysisDate;
		this.inactiveDetections = inactiveDetections;
	}

	public Metadata() {
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getBuild() {
		return build;
	}

	public void setBuild(String build) {
		this.build = build;
	}

	public String getMqeVersion() {
		return mqeVersion;
	}

	public void setMqeVersion(String mqeVersion) {
		this.mqeVersion = mqeVersion;
	}

	public long getTotalAnalysisTime() {
		return totalAnalysisTime;
	}

	public void setTotalAnalysisTime(long totalAnalysisTime) {
		this.totalAnalysisTime = totalAnalysisTime;
	}

	public Date getAnalysisDate() {
		return analysisDate;
	}

	public void setAnalysisDate(Date analysisDate) {
		this.analysisDate = analysisDate;
	}

	public Set<String> getInactiveDetections() {
		return inactiveDetections;
	}

	public void setInactiveDetections(Set<String> inactiveDetections) {
		this.inactiveDetections = inactiveDetections;
	}
}
