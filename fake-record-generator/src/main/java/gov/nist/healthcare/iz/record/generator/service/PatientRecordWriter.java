package gov.nist.healthcare.iz.record.generator.service;

import gov.nist.healthcare.iz.record.generator.model.PatientRecord;

public interface PatientRecordWriter {
    void write(PatientRecord record);
    void flush();
}
