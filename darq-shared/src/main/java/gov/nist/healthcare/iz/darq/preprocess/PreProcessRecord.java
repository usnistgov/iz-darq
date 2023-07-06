package gov.nist.healthcare.iz.darq.preprocess;

import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;

import java.util.Map;

public class PreProcessRecord {
	private AggregatePatientRecord record;
	private String patientAgeGroup;
	private Map<String, String> providersByVaccinationId;
	private Map<String, String> ageGroupAtVaccinationByVaccinationId;

	public PreProcessRecord(AggregatePatientRecord record, String patientAgeGroup, Map<String, String> providersByVaccinationId, Map<String, String> ageGroupAtVaccinationByVaccinationId) {
		this.record = record;
		this.patientAgeGroup = patientAgeGroup;
		this.providersByVaccinationId = providersByVaccinationId;
		this.ageGroupAtVaccinationByVaccinationId = ageGroupAtVaccinationByVaccinationId;
	}

	public AggregatePatientRecord getRecord() {
		return record;
	}

	public void setRecord(AggregatePatientRecord record) {
		this.record = record;
	}

	public String getPatientAgeGroup() {
		return patientAgeGroup;
	}

	public void setPatientAgeGroup(String patientAgeGroup) {
		this.patientAgeGroup = patientAgeGroup;
	}

	public Map<String, String> getProvidersByVaccinationId() {
		return providersByVaccinationId;
	}

	public void setProvidersByVaccinationId(Map<String, String> providersByVaccinationId) {
		this.providersByVaccinationId = providersByVaccinationId;
	}

	public Map<String, String> getAgeGroupAtVaccinationByVaccinationId() {
		return ageGroupAtVaccinationByVaccinationId;
	}

	public void setAgeGroupAtVaccinationByVaccinationId(Map<String, String> ageGroupAtVaccinationByVaccinationId) {
		this.ageGroupAtVaccinationByVaccinationId = ageGroupAtVaccinationByVaccinationId;
	}
}
