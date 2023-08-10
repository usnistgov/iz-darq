package gov.nist.healthcare.iz.darq.patient.matching.service.mismo;

import gov.nist.healthcare.iz.darq.patient.matching.model.mismo.MismoMatchResult;
import gov.nist.healthcare.iz.darq.patient.matching.service.PatientMatcherService;
import org.immregistries.mismo.match.MatchSignatureType;
import org.immregistries.mismo.match.PatientMatchDetermination;
import org.immregistries.mismo.match.PatientMatchResult;
import org.immregistries.mismo.match.PatientMatcher;
import org.immregistries.mismo.match.model.Patient;

public class MismoPatientMatcherService implements PatientMatcherService<Patient, MismoMatchResult> {

  private PatientMatcher patientMatcher;

  public MismoPatientMatcherService(org.immregistries.mismo.match.PatientMatcher patientMatcher) {
	this.patientMatcher = patientMatcher;
  }

  public void configure(Object configuration) throws Exception {
	  if(configuration instanceof  String) {
		  String configurationString = (String) configuration;
		  if(!configurationString.isEmpty()) {
			  this.patientMatcher = new PatientMatcher(configurationString);
		  }
	  } else {
		  throw new Exception("Invalid MISMO configuration");
	  }
  }

  @Override
  public MismoMatchResult match(Patient patientA, Patient patientB) {
	PatientMatchResult result = this.patientMatcher.match(patientA, patientB);
	boolean match = isMatch(result);
	String signature = match ? result.getMatchSignature(MatchSignatureType.PRIMARY).toString() : "";
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

  public boolean isMatch(PatientMatchResult result) {
	return result.getDetermination().equals(PatientMatchDetermination.MATCH) ||
			result.getDetermination().equals(PatientMatchDetermination.POSSIBLE_MATCH);
  }
}
