package gov.nist.healthcare.iz.darq.parser.service;

import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;

public interface Validator<T, R> {

	public T adapt(AggregatePatientRecord record);
	public R validate(T adapted);
	
}
