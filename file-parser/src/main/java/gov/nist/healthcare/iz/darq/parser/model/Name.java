package gov.nist.healthcare.iz.darq.parser.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import gov.nist.healthcare.iz.darq.parser.type.DqString;

public class Name {
	
	@FieldName("First Name")
	public DqString first;
	@FieldName("Middle Name")
	public DqString middle;
	@FieldName("Last Name")
	@DummyValue("Smith")
	public DqString last;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
