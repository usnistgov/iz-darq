package gov.nist.healthcare.iz.darq.parser.model;

import gov.nist.healthcare.iz.darq.parser.annotation.Field;
import org.apache.commons.lang3.builder.ToStringBuilder;

import gov.nist.healthcare.iz.darq.parser.type.DqDate;
import gov.nist.healthcare.iz.darq.parser.type.DqString;

public class VISInformation {

	@Field(name = "Type", index = 0, coded = true, table = "VIS_GDTI")
	public DqString type;
	@Field(name = "Publication Date", index = 1)
	public DqDate publication_date;
	@Field(name = "Presented Date", index = 2)
	public DqDate given_date;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
