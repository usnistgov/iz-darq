package gov.nist.healthcare.iz.darq.patient.matching.service;

public interface PatientBlockHandler<T> {

  void initialize(String path) throws Exception;
  void close() throws Exception;
  void store(String patientId, T patient) throws Exception;
  PatientBlock<T> getCandidates(T patient) throws Exception;

}
