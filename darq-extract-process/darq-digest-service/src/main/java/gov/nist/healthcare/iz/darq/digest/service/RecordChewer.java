package gov.nist.healthcare.iz.darq.digest.service;

import gov.nist.healthcare.iz.darq.detections.DetectionContext;
import gov.nist.healthcare.iz.darq.digest.domain.ADChunk;
import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;
import org.joda.time.LocalDate;

public interface RecordChewer {
	
	ADChunk munch(PreProcessRecord record, LocalDate date, DetectionContext detectionContext) throws Exception;

}
