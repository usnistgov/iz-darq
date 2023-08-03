package gov.nist.healthcare.iz.darq.digest.service.detection.provider.mqe;

import java.util.Objects;

public class VaccineDetection {
	private String reportingGroup;
	private String ageGroup;
	private String code;
	private boolean positive;

	public VaccineDetection(String reportingGroup, String ageGroup, String code, boolean positive) {
		this.reportingGroup = reportingGroup;
		this.ageGroup = ageGroup;
		this.code = code;
		this.positive = positive;
	}

	public String getReportingGroup() {
		return reportingGroup;
	}

	public void setReportingGroup(String reportingGroup) {
		this.reportingGroup = reportingGroup;
	}

	public String getAgeGroup() {
		return ageGroup;
	}

	public void setAgeGroup(String ageGroup) {
		this.ageGroup = ageGroup;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isPositive() {
		return positive;
	}

	public void setPositive(boolean positive) {
		this.positive = positive;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VaccineDetection that = (VaccineDetection) o;
		return Objects.equals(code, that.code);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code);
	}
}
