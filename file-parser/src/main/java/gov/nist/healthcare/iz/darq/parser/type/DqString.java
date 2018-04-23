package gov.nist.healthcare.iz.darq.parser.type;

import org.apache.commons.lang3.RandomStringUtils;

import gov.nist.lightdb.exception.InvalidValueException;

public class DqString extends DataUnit<String> {

	public DqString(String payload) throws InvalidValueException {
		super(payload);
	}

	@Override
	public String validate(String payload) throws InvalidValueException {
		return payload;
	}

	@Override
	protected String dummy(int n) {
		if(n == -1)
			return "PlaceholderData";
		else {
			return RandomStringUtils.random(n);
		}
	}

	@Override
	protected void validatePlaceHolder(DescriptorType placeholder) throws InvalidValueException {
		
	}

}
