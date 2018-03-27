package gov.nist.healthcare.iz.darq.digest.domain;

import java.util.List;
import java.util.Map;

public class ConfigurationPayload {
	
	private List<Range> ageGroups;
	private List<String> detections;
	

	public List<Range> getAgeGroups() {
		return ageGroups;
	}
	public void setAgeGroups(List<Range> ageGroups) {
		this.ageGroups = ageGroups;
	}
	public List<String> getDetections() {
		return detections;
	}
	public void setDetections(List<String> detections) {
		this.detections = detections;
	}
}
