package gov.nist.healthcare.iz.darq.digest.service;

import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;

@FunctionalInterface
public interface TransformService<T> {

	T transform(AggregatePatientRecord record);
	
}
