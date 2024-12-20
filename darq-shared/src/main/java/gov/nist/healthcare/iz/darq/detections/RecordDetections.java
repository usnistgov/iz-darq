package gov.nist.healthcare.iz.darq.detections;

import gov.nist.healthcare.iz.darq.digest.domain.DetectionSum;
import gov.nist.healthcare.iz.darq.parser.model.Patient;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;

import java.util.Map;

public class RecordDetections {
	Patient patient;
	Map<String, DetectionSum> patientDetections;
	Map<VaccineRecord, Map<String, DetectionSum>> vaccinationDetections;
	Map<String, String> possibleRecordDuplicateIdsWithMatchSignature;

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Map<String, DetectionSum> getPatientDetections() {
		return patientDetections;
	}

	public void setPatientDetections(Map<String, DetectionSum> patientDetections) {
		this.patientDetections = patientDetections;
	}

	public Map<String, String> getPossibleRecordDuplicateIdsWithMatchSignature() {
		return possibleRecordDuplicateIdsWithMatchSignature;
	}

	public void setPossibleRecordDuplicateIdsWithMatchSignature(Map<String, String> possibleRecordDuplicateIdsWithMatchSignature) {
		this.possibleRecordDuplicateIdsWithMatchSignature = possibleRecordDuplicateIdsWithMatchSignature;
	}

	public Map<VaccineRecord, Map<String, DetectionSum>> getVaccinationDetections() {
		return vaccinationDetections;
	}

	public void setVaccinationDetections(Map<VaccineRecord, Map<String, DetectionSum>> vaccinationDetections) {
		this.vaccinationDetections = vaccinationDetections;
	}

	public void merge(RecordDetections other) throws Exception {
		if(other.getPatient() != null) {
			if(patient == null) {
				patient = other.getPatient();
			} else if(patient != other.getPatient()) {
				throw new Exception("Cannot merge records with different patients");
			}
		}
		if(other.getPatientDetections() != null) {
			if(patientDetections == null) {
				patientDetections = other.getPatientDetections();
			} else {
				other.getPatientDetections().forEach((key, value) -> patientDetections.merge(key, value, DetectionSum::merge));
			}
		}
		if(other.getVaccinationDetections() != null) {
			if(vaccinationDetections == null) {
				vaccinationDetections = other.getVaccinationDetections();
			} else {
				other.getVaccinationDetections().forEach((key, value) -> {
					vaccinationDetections.merge(key, value, (existing, detections) -> {
						detections.forEach((otherKey, otherValue) -> existing.merge(otherKey, otherValue, DetectionSum::merge));
						return existing;
					});
				});
			}
		}
		if(other.getPossibleRecordDuplicateIdsWithMatchSignature() != null) {
			if(possibleRecordDuplicateIdsWithMatchSignature == null) {
				possibleRecordDuplicateIdsWithMatchSignature = other.getPossibleRecordDuplicateIdsWithMatchSignature();
			} else {
				possibleRecordDuplicateIdsWithMatchSignature.putAll(other.getPossibleRecordDuplicateIdsWithMatchSignature());
			}
		}
	}
}
