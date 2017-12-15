package gov.nist.healthcare.iz.darq.analysis;

import java.util.ArrayList;
import java.util.List;

public class SubjectAnalysis {

	private String fieldOfInterest;
	private List<Statistic> statistics;
	private List<Detection> detections;

	public String getFieldOfInterest() {
		return fieldOfInterest;
	}

	public void setFieldOfInterest(String fieldOfInterest) {
		this.fieldOfInterest = fieldOfInterest;
	}

	public List<Statistic> getStatistics() {
		if (statistics == null) {
			statistics = new ArrayList<>();
		}
		return statistics;
	}

	public void setStatistics(List<Statistic> statistics) {
		this.statistics = statistics;
	}

	public List<Detection> getDetections() {
		return detections;
	}

	public void setDetections(List<Detection> detections) {
		this.detections = detections;
	}

	public void factorIn(SubjectAnalysis sp) {
		if (this.getFieldOfInterest().equals(sp.getFieldOfInterest())) {
			for (Statistic st : sp.getStatistics()) {
				int i = this.getStatistics().indexOf(st);
				if (i != -1) {
					this.getStatistics().get(i).factorIn(st);
				} else {
					this.getStatistics().add(st);
				}
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fieldOfInterest == null) ? 0 : fieldOfInterest.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubjectAnalysis other = (SubjectAnalysis) obj;
		if (fieldOfInterest == null) {
			if (other.fieldOfInterest != null)
				return false;
		} else if (!fieldOfInterest.equals(other.fieldOfInterest))
			return false;
		return true;
	}

}
