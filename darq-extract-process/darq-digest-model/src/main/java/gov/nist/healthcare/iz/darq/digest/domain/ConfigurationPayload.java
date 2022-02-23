package gov.nist.healthcare.iz.darq.digest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ConfigurationPayload {
	
	private List<Range> ageGroups;
	private List<String> detections;
	private String asOf;
	private Map<String, String> vaxCodeAbstraction;
	private final Date now = new Date();

	public List<Range> getAgeGroups() {
		if(this.ageGroups == null) {
			this.ageGroups = new ArrayList<>();
		}
		return ageGroups;
	}
	public void setAgeGroups(List<Range> ageGroups) {
		this.ageGroups = ageGroups;
	}

	public List<String> getDetections() {
		if(this.detections == null) {
			this.detections = new ArrayList<>();
		}
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
			return now;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ConfigurationPayload that = (ConfigurationPayload) o;
		Set<Range> sourceAgeGroups = new HashSet<>(ageGroups);
		Set<Range> targetAgeGroups = new HashSet<>(that.ageGroups);
		Set<String> sourceDetections = new HashSet<>(detections);
		Set<String> targetDetections = new HashSet<>(that.detections);
		return sourceAgeGroups.equals(targetAgeGroups) &&
				sourceDetections.equals(targetDetections) &&
				Objects.equals(asOf, that.asOf) &&
				Objects.equals(vaxCodeAbstraction, that.vaxCodeAbstraction);
	}

	@Override
	public int hashCode() {
		return Objects.hash(ageGroups, detections, asOf, vaxCodeAbstraction);
	}
}
