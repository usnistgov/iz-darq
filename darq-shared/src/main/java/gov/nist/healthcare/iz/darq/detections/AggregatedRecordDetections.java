package gov.nist.healthcare.iz.darq.detections;

import gov.nist.healthcare.iz.darq.digest.domain.DetectionSum;

import java.util.HashMap;
import java.util.Map;

public class AggregatedRecordDetections {

	// Provider, Age Group, DetectionCode
	Map<String, Map<String, Map<String, DetectionSum>>> vaccinations;
	// DetectionCode
	Map<String, DetectionSum> patient;

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
		}
	}
}
