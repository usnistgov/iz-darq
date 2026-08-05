package gov.nist.healthcare.iz.darq.patient.matching.service.mismo;

import gov.nist.healthcare.iz.darq.patient.matching.model.PatientRecord;
import org.immregistries.mismo.match.model.Patient;

public class MismoPatientRecord extends PatientRecord<Patient> {
  public MismoPatientRecord(String id, Patient patient) {
	super(id, patient);
  }
}
