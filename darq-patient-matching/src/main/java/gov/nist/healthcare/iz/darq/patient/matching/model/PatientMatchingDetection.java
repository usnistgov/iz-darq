package gov.nist.healthcare.iz.darq.patient.matching.model;

public enum PatientMatchingDetection {
  PM001("Patient record is possible duplicate");

  private final String message;

  PatientMatchingDetection(String message) {
	this.message = message;
  }

  public String getMessage() {
	return message;
  }

}
