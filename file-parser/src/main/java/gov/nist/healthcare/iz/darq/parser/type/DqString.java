package gov.nist.healthcare.iz.darq.parser.type;

import gov.nist.lightdb.exception.InvalidValueException;

public class DqString extends DataUnit<String> {

	public DqString(String payload) throws InvalidValueException {
		super(payload);
	}

	@Override
	public String validate(String payload) throws InvalidValueException {
		return payload;
	}

}
