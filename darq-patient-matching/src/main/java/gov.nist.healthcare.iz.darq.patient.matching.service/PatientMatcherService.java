package gov.nist.healthcare.iz.darq.patient.matching.service;

import gov.nist.healthcare.iz.darq.patient.matching.model.MatchResult;

public interface PatientMatcherService<T, E extends MatchResult> {

  void configure(Object configuration) throws Exception;
  E match(T patientA, T patientB);
  boolean consider(T patient);

}
