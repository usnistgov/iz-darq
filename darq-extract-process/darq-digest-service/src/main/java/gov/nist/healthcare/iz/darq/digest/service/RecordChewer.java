package gov.nist.healthcare.iz.darq.digest.service;

import gov.nist.healthcare.iz.darq.detections.DetectionContext;
import gov.nist.healthcare.iz.darq.detections.DetectionEngine;
import gov.nist.healthcare.iz.darq.digest.service.patient.matching.PatientMatchingService;
import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;
import org.joda.time.LocalDate;

import gov.nist.healthcare.iz.darq.digest.domain.ADChunk;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;

public interface RecordChewer {
	
	ADChunk munch(PreProcessRecord record, LocalDate date, DetectionContext detectionContext) throws Exception;

}
