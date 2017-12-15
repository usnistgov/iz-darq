package gov.nist.lightdb.service;

import gov.nist.lightdb.domain.Record;
import gov.nist.lightdb.exception.InvalidValueException;

public interface Parser<T extends Record> {

	public T parse(String line) throws InvalidValueException;
	
}
