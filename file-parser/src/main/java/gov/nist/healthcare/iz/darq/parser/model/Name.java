package gov.nist.healthcare.iz.darq.parser.model;

import gov.nist.healthcare.iz.darq.parser.annotation.DummyValue;
import gov.nist.healthcare.iz.darq.parser.annotation.Field;
import gov.nist.healthcare.iz.darq.parser.annotation.FieldName;
import org.apache.commons.lang3.builder.ToStringBuilder;

import gov.nist.healthcare.iz.darq.parser.type.DqString;

public class Name {

	@Field(name="First Name", index = 0)
	public DqString first;
	@Field(name="Middle Name", index = 1)
	public DqString middle;
	@DummyValue("Smith")
	@Field(name="Last Name", index = 2)
	public DqString last;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
