package gov.nist.lightdb.service;

import gov.nist.lightdb.domain.Record;
import gov.nist.lightdb.exception.InvalidValueException;

@FunctionalInterface
public interface Parser<T extends Record> {

	T parse(String line, int lineNumber) throws InvalidValueException;
	
}
