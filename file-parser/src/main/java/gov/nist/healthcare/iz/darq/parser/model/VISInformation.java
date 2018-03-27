package gov.nist.healthcare.iz.darq.parser.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import gov.nist.healthcare.iz.darq.parser.type.DqDate;
import gov.nist.healthcare.iz.darq.parser.type.DqString;

public class VISInformation {
	
	@FieldName("Type")
	public DqString type;
	@FieldName("Publication Date")
	public DqDate publication_date;
	@FieldName("Presented Date")
	public DqDate given_date;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
