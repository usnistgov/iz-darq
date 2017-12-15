package gov.nist.healthcare.iz.darq.parser.type;

import gov.nist.lightdb.exception.InvalidValueException;

public class DqBoolean extends DataUnit<Boolean> {

	public DqBoolean(String payload) throws InvalidValueException {
		super(payload);
	}

	@Override
	protected Boolean validate(String payload) throws InvalidValueException {
		
		if(payload.toLowerCase().equals("true"))
			return true;
		if(payload.toLowerCase().equals("false"))
			return false;
		
		throw new InvalidValueException("'"+payload+"' is an invalid boolean");
	}

}
