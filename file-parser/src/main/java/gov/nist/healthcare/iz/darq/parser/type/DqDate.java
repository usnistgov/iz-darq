package gov.nist.healthcare.iz.darq.parser.type;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import gov.nist.lightdb.exception.InvalidValueException;

public class DqDate extends DataUnit<Date> {
	 
	
	public DqDate(String payload) throws InvalidValueException {
		super(payload);
	}

	@Override
	public Date validate(String payload) throws InvalidValueException {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			format.setLenient(false);
			return format.parse(payload);
		}
		catch(ParseException e){
			throw new InvalidValueException("'"+payload+"' is invalid date format must be yyyyMMdd");
		}
	}
	
}
