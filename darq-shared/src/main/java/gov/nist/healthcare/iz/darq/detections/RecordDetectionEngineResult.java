package gov.nist.healthcare.iz.darq.detections;

import gov.nist.healthcare.iz.darq.digest.domain.DetectionSum;

import java.util.HashMap;
import java.util.Map;

public class RecordDetectionEngineResult {
	Map<String, DetectionSum> patientDetections = new HashMap<String, DetectionSum>();
	Map<String, Map<String, DetectionSum>> vaccinationDetectionsById = new HashMap<>();
	Map<String, String> possiblePatientRecordDuplicatesWithSignature = new HashMap<>();


	public Map<String, DetectionSum> getPatientDetections() {
		return patientDetections;
	}

	public void setPatientDetections(Map<String, DetectionSum> patientDetections) {
		this.patientDetections = patientDetections;
	}

	public Map<String, String> getPossiblePatientRecordDuplicatesWithSignature() {
		return possiblePatientRecordDuplicatesWithSignature;
	}

	public void setPossiblePatientRecordDuplicatesWithSignature(Map<String, String> possiblePatientRecordDuplicatesWithSignature) {
		this.possiblePatientRecordDuplicatesWithSignature = possiblePatientRecordDuplicatesWithSignature;
	}

	public Map<String, Map<String, DetectionSum>> getVaccinationDetectionsById() {
		return vaccinationDetectionsById;
	}

	public void setVaccinationDetectionsById(Map<String, Map<String, DetectionSum>> vaccinationDetectionsById) {
		this.vaccinationDetectionsById = vaccinationDetectionsById;
	}

	public void merge(RecordDetectionEngineResult other) {
		if(other.getPatientDetections() != null) {
			if(patientDetections == null) {
				patientDetections = other.getPatientDetections();
			} else {
				other.getPatientDetections().forEach((key, value) -> patientDetections.merge(key, value, DetectionSum::merge));
			}
		}
		if(other.getVaccinationDetectionsById() != null) {
			if(vaccinationDetectionsById == null) {
				vaccinationDetectionsById = other.getVaccinationDetectionsById();
			} else {
				other.getVaccinationDetectionsById().forEach((key, value) -> {
					vaccinationDetectionsById.merge(key, value, (existing, detections) -> {
						detections.forEach((otherKey, otherValue) -> existing.merge(otherKey, otherValue, DetectionSum::merge));
						return existing;
					});
				});
			}
		}
		if(other.getPossiblePatientRecordDuplicatesWithSignature() != null) {
			if(possiblePatientRecordDuplicatesWithSignature == null) {
				possiblePatientRecordDuplicatesWithSignature = other.getPossiblePatientRecordDuplicatesWithSignature();
			} else {
				possiblePatientRecordDuplicatesWithSignature.putAll(other.getPossiblePatientRecordDuplicatesWithSignature());
			}
		}
	}
}
