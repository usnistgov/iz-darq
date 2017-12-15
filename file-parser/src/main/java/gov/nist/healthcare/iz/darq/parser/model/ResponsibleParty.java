package gov.nist.healthcare.iz.darq.parser.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import gov.nist.healthcare.iz.darq.parser.type.DqString;

public class ResponsibleParty {
	
	public DqString relationshipToPatient;
	public Name name = new Name();

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
