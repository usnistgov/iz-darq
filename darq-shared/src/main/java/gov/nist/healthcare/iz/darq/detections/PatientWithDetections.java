package gov.nist.healthcare.iz.darq.detections;

import gov.nist.healthcare.iz.darq.digest.domain.DetectionSum;
import gov.nist.healthcare.iz.darq.parser.model.Patient;

import java.util.Map;

public class PatientWithDetections {
	private Patient patient;
	private Map<String, DetectionSum> detections;

	public PatientWithDetections(Patient patient) {
		this.patient = patient;
	}

	public Map<String, DetectionSum> getDetections() {
		return detections;
	}

	public void setDetections(Map<String, DetectionSum> detections) {
		this.detections = detections;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}
}
