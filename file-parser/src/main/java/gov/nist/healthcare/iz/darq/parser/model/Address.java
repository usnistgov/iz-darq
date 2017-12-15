package gov.nist.healthcare.iz.darq.parser.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import gov.nist.healthcare.iz.darq.parser.type.DqString;

public class Address {
	
	public DqString street;
	public DqString country;
	public DqString city;
	public DqString state;
	public DqString zip;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
