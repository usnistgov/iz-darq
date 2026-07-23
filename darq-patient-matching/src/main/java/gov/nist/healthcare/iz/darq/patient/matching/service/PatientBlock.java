package gov.nist.healthcare.iz.darq.patient.matching.service;

import gov.nist.healthcare.iz.darq.patient.matching.model.EndOfBlockException;
import gov.nist.healthcare.iz.darq.patient.matching.model.PatientRecord;

public interface PatientBlock<T> {
  <E extends PatientRecord<T>> E next() throws EndOfBlockException;
}
