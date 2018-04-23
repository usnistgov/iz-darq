package gov.nist.healthcare.iz.darq.parser.type;

import gov.nist.lightdb.exception.InvalidValueException;

public class DqNumeric extends DataUnit<Integer> {
	
	public DqNumeric(String payload) throws InvalidValueException {
		super(payload);
	}

	@Override
	public Integer validate(String payload) throws InvalidValueException {
		try{
			return Integer.parseInt(payload);
		}
		catch(Exception e){
			throw new InvalidValueException("'"+payload+"' is not a valid numeric value");
		}		
	}

	@Override
	protected Integer dummy(int n) {
		return 99;
	}

	@Override
	protected void validatePlaceHolder(DescriptorType placeholder) throws InvalidValueException {
		if(placeholder.equals(DescriptorType.VALUE_LENGTH)){
			throw new InvalidValueException("Cannot use VALUE_LENGTH metadata on a numeric field");
		}
	}


}
