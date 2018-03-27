package gov.nist.healthcare.iz.darq.digest.domain;

import java.util.Date;
import java.util.Map;

public class ADFile {
	
	private Date analysisDate;
	private Map<String, Fraction> extraction;
	private Map<String, PatientPayload> patients;
	private Map<String, Map<String, VaccinationPayload>> vaccinations;
	private ConfigurationPayload configuration;
	private Summary summary;
	
	public ADFile(Map<String, Fraction> extraction, Map<String, PatientPayload> patients,
			Map<String, Map<String, VaccinationPayload>> vaccinations, ConfigurationPayload configuration, Summary summary) {
		super();
		this.extraction = extraction;
		this.patients = patients;
		this.vaccinations = vaccinations;
		this.configuration = configuration;
		this.analysisDate = new Date();
		this.summary = summary;
		
	}
	
	
	public ADFile() {
		super();
	}
	

	public Map<String, Fraction> getExtraction() {
		return extraction;
	}
	public void setExtraction(Map<String, Fraction> extraction) {
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
	

	
}
