package gov.nist.healthcare.iz.darq.parser.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import gov.nist.healthcare.iz.darq.parser.type.DqString;

public class Address {
	

	@FieldName("Street Address")
	public DqString street;
	@FieldName("Country")
	public DqString country;
	@FieldName("City")
	public DqString city;
	@FieldName("State")
	public DqString state;
	@FieldName("ZIP")
	public DqString zip;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
