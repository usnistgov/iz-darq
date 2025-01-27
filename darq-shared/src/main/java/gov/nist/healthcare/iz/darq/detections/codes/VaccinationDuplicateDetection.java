package gov.nist.healthcare.iz.darq.detections.codes;

public enum VaccinationDuplicateDetection {
	VD0001("Vaccination is duplicated");

	private final String message;

	VaccinationDuplicateDetection(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
