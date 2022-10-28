package gov.nist.healthcare.iz.darq.digest.domain;

import java.util.Date;
import java.util.Map;
import java.util.Set;

public class ADFile {

	private long totalAnalysisTime;
	private Date analysisDate;
	private Map<String, PatientPayload> generalPatientPayload;
	private Map<String, Map<String, ADPayload>> reportingGroupPayload;
	private ConfigurationPayload configuration;
	private Summary summary;
	private String version;
	private String build;
	private String mqeVersion;
	private Set<String> inactiveDetections;
	private int historical = 0;
	private int administered = 0;

	public ADFile(
			Map<String, PatientPayload> generalPatientPayload,
			Map<String, Map<String, ADPayload>> reportingGroupPayload,
			ConfigurationPayload configuration,
			Summary summary,
			String version,
			String build,
			String mqeVersion,
			Set<String> inactiveDetections,
			int historical,
			int administered
	) {
		super();
		this.generalPatientPayload = generalPatientPayload;
		this.reportingGroupPayload = reportingGroupPayload;
		this.configuration = configuration;
		this.analysisDate = new Date();
		this.summary = summary;
		this.version = version;
		this.build = build;
		this.mqeVersion = mqeVersion;
		this.inactiveDetections = inactiveDetections;
		this.historical = historical;
		this.administered = administered;
	}

	public ADFile(
			Map<String, PatientPayload> generalPatientPayload,
			Map<String, Map<String, ADPayload>> reportingGroupPayload,
			ConfigurationPayload configuration,
			Summary summary,
			String version,
			String build,
			String mqeVersion,
			Set<String> inactiveDetections,
			long elapsed,
			int historical,
			int administered
	) {
		this(
				generalPatientPayload,
				reportingGroupPayload,
				configuration, summary,
				version,
				build,
				mqeVersion,
				inactiveDetections,
				historical,
				administered
		);
		this.totalAnalysisTime = elapsed;
	}
	
	
	public ADFile() {
		super();
	}

	public Map<String, PatientPayload> getGeneralPatientPayload() {
		return generalPatientPayload;
	}

	public void setGeneralPatientPayload(Map<String, PatientPayload> generalPatientPayload) {
		this.generalPatientPayload = generalPatientPayload;
	}

	public Map<String, Map<String, ADPayload>> getReportingGroupPayload() {
		return reportingGroupPayload;
	}

	public void setReportingGroupPayload(Map<String, Map<String, ADPayload>> reportingGroupPayload) {
		this.reportingGroupPayload = reportingGroupPayload;
	}

	public Date getAnalysisDate() {
		return analysisDate;
	}
	public void setAnalysisDate(Date analysisDate) {
		this.analysisDate = analysisDate;
	}
	public ConfigurationPayload getConfiguration() {
		return configuration;
	}
	public void setConfiguration(ConfigurationPayload configuration) {
		this.configuration = configuration;
	}
	public Summary getSummary() {
		return summary;
	}
	public void setSummary(Summary summary) {
		this.summary = summary;
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

	public Set<String> getInactiveDetections() {
		return inactiveDetections;
	}

	public void setInactiveDetections(Set<String> inactiveDetections) {
		this.inactiveDetections = inactiveDetections;
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

	public int getHistorical() {
		return historical;
	}

	public void setHistorical(int historical) {
		this.historical = historical;
	}

	public int getAdministered() {
		return administered;
	}

	public void setAdministered(int administered) {
		this.administered = administered;
	}
}
