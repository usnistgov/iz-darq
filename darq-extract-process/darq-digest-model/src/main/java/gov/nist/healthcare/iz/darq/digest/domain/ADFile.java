package gov.nist.healthcare.iz.darq.digest.domain;

import java.util.Date;
import java.util.Map;
import java.util.Set;

public class ADFile {
	
	public static class Vocabulary {
		private Map<Field, Set<String>> byField;
		private Map<String, Set<String>> byTable;
		
		public Vocabulary() {
			super();
		}
		public Vocabulary(Map<Field, Set<String>> byField, Map<String, Set<String>> byTable) {
			super();
			this.byField = byField;
			this.byTable = byTable;
		}
		public Map<Field, Set<String>> getByField() {
			return byField;
		}
		public void setByField(Map<Field, Set<String>> byField) {
			this.byField = byField;
		}
		public Map<String, Set<String>> getByTable() {
			return byTable;
		}
		public void setByTable(Map<String, Set<String>> byTable) {
			this.byTable = byTable;
		}
	}
	
	private long totalAnalysisTime;
	private Date analysisDate;
	private Map<String, PatientPayload> generalPatientPayload;
	private Map<String, Map<String, ADPayload>> reportingGroupPayload;
	private ConfigurationPayload configuration;
	private Summary summary;
	private Vocabulary vocabulary;
	private String version;
	private String build;
	private String mqeVersion;
	private Set<String> inactiveDetections;

	public ADFile(
			Map<String, PatientPayload> generalPatientPayload,
			Map<String, Map<String, ADPayload>> reportingGroupPayload,
			ConfigurationPayload configuration,
			Summary summary,
			Vocabulary vocabulary,
			String version,
			String build,
			String mqeVersion,
			Set<String> inactiveDetections
	) {
		super();
		this.generalPatientPayload = generalPatientPayload;
		this.reportingGroupPayload = reportingGroupPayload;
		this.configuration = configuration;
		this.analysisDate = new Date();
		this.summary = summary;
		this.vocabulary = vocabulary;
		this.version = version;
		this.build = build;
		this.mqeVersion = mqeVersion;
		this.inactiveDetections = inactiveDetections;
	}

	public ADFile(
			Map<String, PatientPayload> generalPatientPayload,
			Map<String, Map<String, ADPayload>> reportingGroupPayload,
			ConfigurationPayload configuration,
			Summary summary,
			Vocabulary vocabulary,
			String version,
			String build,
			String mqeVersion,
			Set<String> inactiveDetections,
			long elapsed
	) {
		this(
				generalPatientPayload,
				reportingGroupPayload,
				configuration, summary,
				vocabulary,
				version,
				build,
				mqeVersion,
				inactiveDetections
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
	public Vocabulary getVocabulary() {
		return vocabulary;
	}
	public void setVocabulary(Vocabulary vocabulary) {
		this.vocabulary = vocabulary;
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
}
