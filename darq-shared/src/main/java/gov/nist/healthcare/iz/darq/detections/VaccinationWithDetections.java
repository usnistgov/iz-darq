package gov.nist.healthcare.iz.darq.detections;

import gov.nist.healthcare.iz.darq.digest.domain.DetectionSum;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;

import java.util.HashMap;
import java.util.Map;

public class VaccinationWithDetections {
	private VaccineRecord record;
	private Map<String, DetectionSum> detections = new HashMap<>();

	public VaccinationWithDetections(VaccineRecord record) {
		this.record = record;
	}

	public Map<String, DetectionSum> getDetections() {
		return detections;
	}

	public void setDetections(Map<String, DetectionSum> detections) {
		this.detections = detections;
	}

	public VaccineRecord getRecord() {
		return record;
	}

	public void setRecord(VaccineRecord record) {
		this.record = record;
	}
}
