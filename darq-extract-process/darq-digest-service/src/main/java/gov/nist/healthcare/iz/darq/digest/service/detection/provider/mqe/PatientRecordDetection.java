package gov.nist.healthcare.iz.darq.digest.service.detection.provider.mqe;

import java.util.Objects;

public class PatientRecordDetection {
	private String code;
	private final boolean positive;

	public PatientRecordDetection(String code, boolean positive) {
		this.code = code;
		this.positive = positive;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PatientRecordDetection that = (PatientRecordDetection) o;
		return Objects.equals(code, that.code);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code);
	}
}
