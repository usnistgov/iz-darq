package gov.nist.healthcare.iz.darq.patient.matching.service.mismo;

import gov.nist.healthcare.iz.darq.patient.matching.model.mismo.MismoMatchResult;
import gov.nist.healthcare.iz.darq.patient.matching.service.PatientMatcherService;
import org.immregistries.mismo.match.PatientMatchDetermination;
import org.immregistries.mismo.match.PatientMatcher;
import org.immregistries.mismo.match.model.Patient;

public class MismoPatientMatcherService implements PatientMatcherService<Patient, MismoMatchResult> {

  private final PatientMatcher patientMatcher;

  public MismoPatientMatcherService(org.immregistries.mismo.match.PatientMatcher patientMatcher) {
	this.patientMatcher = patientMatcher;
  }

  @Override
  public MismoMatchResult match(Patient patientA, Patient patientB) {
	PatientMatchDetermination result = this.patientMatcher.match(patientA, patientB);
	boolean match = isMatch(result);
	String signature = match ? this.patientMatcher.generateSignature(patientA, patientB) : "";
	return new MismoMatchResult(match, signature);
  }

  // Record should have a minimum of first name, last name and dob
  @Override
  public boolean consider(Patient patient) {
	return hasValue(patient.getNameFirst()) && hasValue(patient.getNameLast()) && hasValue(patient.getBirthDate());
  }

  public boolean hasValue(String value) {
	return value != null && !value.isEmpty();
  }

  public boolean isMatch(PatientMatchDetermination determination) {
	return determination.equals(PatientMatchDetermination.MATCH) ||
			determination.equals(PatientMatchDetermination.POSSIBLE_MATCH);
  }
}
