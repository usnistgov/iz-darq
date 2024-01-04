package gov.nist.healthcare.iz.darq.patient.matching.model;

public class PatientRecord<T> {
  private String id;
  private T patient;

  public PatientRecord(String id, T patient) {
	this.id = id;
	this.patient = patient;
  }

  public T getPatient() {
	return patient;
  }

  public void setPatient(T patient) {
	this.patient = patient;
  }

  public String getId() {
	return id;
  }

  public void setId(String id) {
	this.id = id;
  }
}
