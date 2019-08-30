package gov.nist.healthcare.iz.darq.parser.model;

import gov.nist.healthcare.iz.darq.parser.annotation.Field;
import org.apache.commons.lang3.builder.ToStringBuilder;

import gov.nist.healthcare.iz.darq.parser.type.DqString;

public class Address {
	
	@Field(name = "Street Address", index = 0)
	public DqString street;
	@Field(name = "Country", index = 3, coded = true, table = "HL70399")
	public DqString country;
	@Field(name = "City", index = 1)
	public DqString city;
	@Field(name = "State", index = 2, coded = true, table = "US_POSTAL_STATE")
	public DqString state;
	@Field(name = "ZIP", index = 4)
	public DqString zip;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
