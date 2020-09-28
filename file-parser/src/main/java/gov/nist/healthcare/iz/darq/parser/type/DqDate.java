package gov.nist.healthcare.iz.darq.parser.type;

import gov.nist.healthcare.iz.darq.parser.exception.InvalidValueException;
import org.joda.time.LocalDate;


public class DqDate extends DataUnit<LocalDate> {

	private final DqDateFormat dateFormat;

	public DqDate(String payload, DqDateFormat dateFormat) throws InvalidValueException {
		super();
		this.dateFormat = dateFormat;
		this.set(payload);
	}

	@Override
	public LocalDate validate(String payload) throws InvalidValueException {
		try {
			return dateFormat.getDate(payload);
		}
		catch(Exception e){

			throw new InvalidValueException("'"+payload+"' is invalid date format must be " + dateFormat.getPattern());
		}
	}

	@Override
	protected LocalDate dummy(int n) {
		return null;
	}

	@Override
	protected LocalDate empty() {
		return null;
	}

	@Override
	protected void validatePlaceHolder(DescriptorType placeholder) throws InvalidValueException {
		if(placeholder.equals(DescriptorType.VALUE_LENGTH) || placeholder.equals(DescriptorType.VALUE_PRESENT)){
			throw new InvalidValueException("Cannot use metadata VALUE_PRESENT or VALUE_LENGTH on a date FIELD (unable to generate a meaningful value)");
		}
	}
	
}
