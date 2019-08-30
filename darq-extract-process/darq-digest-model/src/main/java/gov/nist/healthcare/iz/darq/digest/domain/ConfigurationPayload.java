package gov.nist.healthcare.iz.darq.digest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ConfigurationPayload {
	
	private List<Range> ageGroups;
	private List<String> detections;
	private String asOf;
	private Map<String, String> vaxCodeAbstraction;

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
	public Map<String, String> getVaxCodeAbstraction() {
		return vaxCodeAbstraction;
	}
	public void setVaxCodeAbstraction(Map<String, String> vaxCodeAbstraction) {
		this.vaxCodeAbstraction = vaxCodeAbstraction;
	}

	public String getAsOf() {
		return asOf;
	}

	public void setAsOf(String asOf) {
		this.asOf = asOf;
	}

	@JsonIgnore
	public Date getAsOfDate() throws ParseException {
		if(this.asOf != null && !this.asOf.isEmpty()) {
			return  (new SimpleDateFormat("MM/dd/yyyy")).parse(this.asOf);
		} else {
			return new Date();
		}
	}
}
