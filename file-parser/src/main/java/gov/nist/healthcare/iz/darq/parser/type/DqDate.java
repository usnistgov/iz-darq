package gov.nist.healthcare.iz.darq.parser.type;

import gov.nist.healthcare.iz.darq.parser.exception.InvalidValueException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DqDate extends DataUnit<Date> {
	 
	
	public DqDate(String payload) throws InvalidValueException {
		super(payload);
	}

	@Override
	public Date validate(String payload) throws InvalidValueException {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			format.setLenient(false);
			return format.parse(payload);
		}
		catch(ParseException e){
			throw new InvalidValueException("'"+payload+"' is invalid date format must be yyyy-MM-dd");
		}
	}

	@Override
	protected Date dummy(int n) {
		return null;
	}

	@Override
	protected Date empty() {
		return null;
	}

	@Override
	protected void validatePlaceHolder(DescriptorType placeholder) throws InvalidValueException {
		if(placeholder.equals(DescriptorType.VALUE_LENGTH) || placeholder.equals(DescriptorType.VALUE_PRESENT)){
			throw new InvalidValueException("Cannot use metadata VALUE_PRESENT or VALUE_LENGTH on a date FIELD (unable to generate a meaningful value)");
		}
	}
	
}
