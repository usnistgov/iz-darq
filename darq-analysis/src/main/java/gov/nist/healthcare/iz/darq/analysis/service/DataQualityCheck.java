package gov.nist.healthcare.iz.darq.analysis.service;

import java.util.List;

import gov.nist.healthcare.iz.darq.analysis.Detection;
import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;

public interface DataQualityCheck {
	
	 List<Detection> inspect(AggregatePatientRecord patientRecord);

}
