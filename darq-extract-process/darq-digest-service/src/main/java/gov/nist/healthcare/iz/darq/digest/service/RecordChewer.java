package gov.nist.healthcare.iz.darq.digest.service;

import org.joda.time.LocalDate;

import gov.nist.healthcare.iz.darq.digest.domain.ADChunk;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;

public interface RecordChewer {
	
	ADChunk munch(ConfigurationProvider configuration, AggregatePatientRecord apr, LocalDate date) throws Exception;

}
