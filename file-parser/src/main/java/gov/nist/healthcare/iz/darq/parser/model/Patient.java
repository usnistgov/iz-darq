package gov.nist.healthcare.iz.darq.parser.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import gov.nist.healthcare.iz.darq.parser.type.DqDate;
import gov.nist.healthcare.iz.darq.parser.type.DqNumeric;
import gov.nist.healthcare.iz.darq.parser.type.DqString;
import gov.nist.lightdb.domain.Record;

public class Patient extends Record {
	
	@FieldName("Record ID")
	public DqString patID;
	@FieldName("Name")
	public Name own_name = new Name();
	@FieldName("Mother's Name")
	public Name mother_name = new Name();
	@FieldName("Alias")
	public Name alias_name  = new Name();
	@FieldName("Responsible Party")
	public ResponsibleParty responsible_party = new ResponsibleParty();
	@FieldName("Address")
	public Address address = new Address();
	@FieldName("Date Of Birth")
	public DqDate date_of_birth;
	@FieldName("Gender")
	@Code("GENDER")
	public DqString gender;
	@FieldName("Mother's Maiden Name")
	public DqString mother_maiden_name;
	@FieldName("Language")
	public DqString language;
	@FieldName("Email Address")
	public DqString email_address;
	@FieldName("Phone Number")
	public DqString phone;
	@FieldName("Ethnicity Code")
	@Code("ETH")
	public DqNumeric ethnicity_code;
	@FieldName("Race Code")
	@Code("RACE")
	public DqNumeric race_code;
	@FieldName("Birth Facility Name")
	public DqString birth_facility_name;
	@FieldName("Birth Multiple Indicator")
	public DqString multi_birth_indicator;
	@FieldName("Birth Order")
	public DqNumeric birth_order;
	@FieldName("Provider Facility Level")
	public DqString provider_facility_level;
	@FieldName("Provider IIS Level")
	public DqString iis_level;
	
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}


	@Override
	public String getID() {
		return patID.getValue();
	}
}
