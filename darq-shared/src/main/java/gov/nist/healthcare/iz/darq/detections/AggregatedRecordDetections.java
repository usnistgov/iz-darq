package gov.nist.healthcare.iz.darq.detections;

import gov.nist.healthcare.iz.darq.digest.domain.DetectionSum;

import java.util.HashMap;
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

	public void merge(AggregatedRecordDetections detections) {
		if(detections != null) {
			if(detections.patient != null) {
				if(patient == null) {
					patient = new HashMap<>();
				}
				detections.patient.forEach((key, value) -> patient.merge(key, value, DetectionSum::merge));
			}
			if(detections.vaccinations != null) {
				if(vaccinations == null) {
					vaccinations = new HashMap<>();
				}
				detections.vaccinations.forEach((keyProvider, valueProvider) -> {
					vaccinations.merge(keyProvider, valueProvider, (existingByAgeGroup, valueByAgeGroup) -> {
						valueByAgeGroup.forEach((keyAgeGroup, valueAgeGroup) -> {
							existingByAgeGroup.merge(keyAgeGroup, valueAgeGroup, (existingByDetectionCode, valueByDetectionCode) -> {
								valueByDetectionCode.forEach((keyDetectionCode, valueDetectionCode) -> {
									existingByDetectionCode.merge(keyDetectionCode, valueDetectionCode, DetectionSum::merge);
								});
								return existingByDetectionCode;
							});
						});
						return existingByAgeGroup;
					});
				});
			}
			if(detections.recordMatchSignatures != null) {
				if(recordMatchSignatures == null) {
					recordMatchSignatures = new HashMap<>();
				}
				detections.recordMatchSignatures.forEach((key, value) -> recordMatchSignatures.compute(key, (k, v) -> v == null ? value : v + value));
			}
		}
	}
}
