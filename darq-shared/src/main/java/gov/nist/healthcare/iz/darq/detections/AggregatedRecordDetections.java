package gov.nist.healthcare.iz.darq.detections;

import gov.nist.healthcare.iz.darq.digest.domain.DetectionSum;

import java.util.Map;

public class AggregatedRecordDetections {

	// Provider, Age Group, DetectionCode
	Map<String, Map<String, Map<String, DetectionSum>>> vaccinations;
	// DetectionCode
	Map<String, DetectionSum> patient;
	// Patient Match Signature
	Map<String, Integer> recordMatchSignatures;
	// Duplicate Record ID, Match Signature
	Map<String, String> duplicates;

	public Map<String, Map<String, Map<String, DetectionSum>>> getVaccinations() {
		return vaccinations;
	}

	public void setVaccinations(Map<String, Map<String, Map<String, DetectionSum>>> vaccinations) {
		this.vaccinations = vaccinations;
	}

	public Map<String, DetectionSum> getPatient() {
		return patient;
	}

	public void setPatient(Map<String, DetectionSum> patient) {
		this.patient = patient;
	}

	public Map<String, Integer> getRecordMatchSignatures() {
		return recordMatchSignatures;
	}

	public void setRecordMatchSignatures(Map<String, Integer> recordMatchSignatures) {
		this.recordMatchSignatures = recordMatchSignatures;
	}

	public Map<String, String> getDuplicates() {
		return duplicates;
	}

	public void setDuplicates(Map<String, String> duplicates) {
		this.duplicates = duplicates;
	}
}
