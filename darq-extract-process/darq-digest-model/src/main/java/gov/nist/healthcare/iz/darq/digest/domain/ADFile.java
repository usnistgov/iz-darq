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
	
	
	private Date analysisDate;
	private Map<String, ExtractFraction> extraction;
	private Map<String, PatientPayload> patients;
	private Map<String, Map<String, VaccinationPayload>> vaccinations;
	private ConfigurationPayload configuration;
	private Summary summary;
	private Vocabulary vocabulary;
	
	public ADFile(Map<String, ExtractFraction> extraction, Map<String, PatientPayload> patients,
			Map<String, Map<String, VaccinationPayload>> vaccinations, ConfigurationPayload configuration, Summary summary, Vocabulary vocabulary) {
		super();
		this.extraction = extraction;
		this.patients = patients;
		this.vaccinations = vaccinations;
		this.configuration = configuration;
		this.analysisDate = new Date();
		this.summary = summary;
		this.vocabulary = vocabulary;
	}
	
	
	public ADFile() {
		super();
	}
	

	public Map<String, ExtractFraction> getExtraction() {
		return extraction;
	}
	public void setExtraction(Map<String, ExtractFraction> extraction) {
		this.extraction = extraction;
	}
	public Map<String, PatientPayload> getPatients() {
		return patients;
	}
	public void setPatients(Map<String, PatientPayload> patients) {
		this.patients = patients;
	}
	public Map<String, Map<String, VaccinationPayload>> getVaccinations() {
		return vaccinations;
	}
	public void setVaccinations(Map<String, Map<String, VaccinationPayload>> vaccinations) {
		this.vaccinations = vaccinations;
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
}
