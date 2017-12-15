package gov.nist.healthcare.iz.darq.parser.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import gov.nist.healthcare.iz.darq.parser.type.DqString;

public class Name {
	
	public DqString first;
	public DqString middle;
	public DqString last;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
