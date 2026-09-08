package gov.nist.healthcare.iz.darq.parser.model;

import gov.nist.healthcare.iz.darq.parser.annotation.Field;
import gov.nist.healthcare.iz.darq.parser.type.DqString;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ResponsibleParty {

	@Field(name = "Relationship To Patient", index = 3, coded = true, table = "REL_0063")
	public DqString relationshipToPatient;
	@Field(name = "Name", index = 0)
	public Name name = new Name();

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
