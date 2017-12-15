package gov.nist.healthcare.iz.darq.parser.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import gov.nist.healthcare.iz.darq.parser.type.DqDate;
import gov.nist.healthcare.iz.darq.parser.type.DqNumeric;
import gov.nist.healthcare.iz.darq.parser.type.DqString;
import gov.nist.lightdb.domain.Record;

public class Patient extends Record {
	
	public DqString patID;
	
	public Name own_name = new Name();
	public Name mother_name = new Name();
	public Name alias_name  = new Name();
	public ResponsibleParty responsible_party = new ResponsibleParty();
	public Address address = new Address();
	
	public DqDate date_of_birth;
	public DqString gender;
	public DqString mother_maiden_name;
	public DqString language;
	public DqString email_address;
	public DqString phone;
	public DqNumeric ethnicity_codes;
	public DqNumeric race_codes;
	public DqString birth_facility_name;
	public DqString multi_birth_indicator;
	public DqNumeric birth_order;
	public DqString provider_facility_level;
	public DqString iis_level;
	
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
