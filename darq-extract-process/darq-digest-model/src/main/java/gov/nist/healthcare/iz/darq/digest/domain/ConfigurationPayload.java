package gov.nist.healthcare.iz.darq.digest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nist.healthcare.iz.darq.digest.domain.expression.ComplexDetection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ConfigurationPayload {
	
	private List<Range> ageGroups;
	private List<String> detections;
	private String asOf;
	private boolean activatePatientMatching;
	private String mismoPatientMatchingConfiguration;
	private Map<String, String> vaxCodeAbstraction;
	private final Date now = new Date();
	private List<ComplexDetection> complexDetections = new ArrayList<>();

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

	public boolean isActivatePatientMatching() {
		return activatePatientMatching;
	}

	public void setActivatePatientMatching(boolean activatePatientMatching) {
		this.activatePatientMatching = activatePatientMatching;
	}

	public String getAsOf() {
		return asOf;
	}

	public void setAsOf(String asOf) {
		this.asOf = asOf;
	}

	public String getMismoPatientMatchingConfiguration() {
		return mismoPatientMatchingConfiguration;
	}

	public void setMismoPatientMatchingConfiguration(String mismoPatientMatchingConfiguration) {
		this.mismoPatientMatchingConfiguration = mismoPatientMatchingConfiguration;
	}

	public List<ComplexDetection> getComplexDetections() {
		return complexDetections;
	}

	public void setComplexDetections(List<ComplexDetection> complexDetections) {
		this.complexDetections = complexDetections;
	}

	@JsonIgnore
	public Set<String> getAllDetectionCodes() {
		Set<String> detectionCodes = new HashSet<>();
		detectionCodes.addAll(getDetections());
		if(complexDetections != null) {
			for(ComplexDetection detection : complexDetections) {
				detectionCodes.addAll(detection.getExpression().getLeafDetectionCodes());
				detectionCodes.add(detection.getCode());
			}
		}
		return detectionCodes;
	}

	@JsonIgnore
	public Date getAsOfDate() throws ParseException {
		if(this.asOf != null && !this.asOf.isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			sdf.setLenient(false);
			return  sdf.parse(this.asOf);
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
				Objects.equals(activatePatientMatching, that.activatePatientMatching) &&
				Objects.equals(mismoPatientMatchingConfiguration, that.mismoPatientMatchingConfiguration) &&
				Objects.equals(asOf, that.asOf) &&
				Objects.equals(vaxCodeAbstraction, that.vaxCodeAbstraction) &&
				Objects.equals(complexDetections, that.complexDetections);
	}

	@Override
	public int hashCode() {
		return Objects.hash(ageGroups, detections, asOf, activatePatientMatching, mismoPatientMatchingConfiguration, vaxCodeAbstraction, complexDetections);
	}
}
