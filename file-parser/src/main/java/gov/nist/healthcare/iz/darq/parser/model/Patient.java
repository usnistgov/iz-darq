package gov.nist.healthcare.iz.darq.parser.model;
import gov.nist.healthcare.iz.darq.parser.annotation.Field;
import org.apache.commons.lang3.builder.ToStringBuilder;

import gov.nist.healthcare.iz.darq.parser.type.DqDate;
import gov.nist.healthcare.iz.darq.parser.type.DqNumeric;
import gov.nist.healthcare.iz.darq.parser.type.DqString;

@gov.nist.healthcare.iz.darq.parser.annotation.Record(size = 34, name = "Patient Record")
public class Patient extends gov.nist.healthcare.iz.darq.parser.service.model.Record {
	
	@Field(name="Record ID", index = 0, required = true)
	public DqString patID;
	@Field(name="Name", index = 1)
	public Name own_name = new Name();
	@Field(name="Mother's Name", index = 5)
	public Name mother_name = new Name();
	@Field(name="Alias", index = 20)
	public Name alias_name  = new Name();
	@Field(name="Responsible Party", index = 23)
	public ResponsibleParty responsible_party = new ResponsibleParty();
	@Field(name="Address", index = 10)
	public Address address = new Address();
	@Field(name="Date Of Birth", index = 8)
	public DqDate date_of_birth;
	@Field(name="Gender", index = 9, coded = true, table = "GENDER_0001")
	public DqString gender;
	@Field(name="Mother's Maiden Name", index = 4)
	public DqString mother_maiden_name;
	@Field(name="Language", index = 19, coded = true, table = "LANG_0296")
	public DqString language;
	@Field(name="Email Address", index = 18)
	public DqString email_address;
	@Field(name="Phone Number", index = 17)
	public DqString phone;
	@Field(name="Ethnicity Code", index = 16, coded = true, table = "ETH_0189")
	public DqString ethnicity_code;
	@Field(name="Race Code", index = 15, coded = true, table = "RACE_0005")
	public DqString race_code;
	@Field(name="Birth State", index = 29, coded = true, table = "US_POSTAL_STATE")
	public DqString birth_facility_name;
	@Field(name="Birth Multiple Indicator", index = 27, coded = true, table = "MULTI_BIRTH")
	public DqString multi_birth_indicator;
	@Field(name="Birth Order", index = 28)
	public DqNumeric birth_order;
	@Field(name="Provider Facility Level", index = 30, coded = true, table = "PSTAT_0441")
	public DqString provider_facility_level;
	@Field(name="Provider IIS Level", index = 31, coded = true, table = "PSTAT_0441")
	public DqString iis_level;
	@Field(name="Record Creation Date", index = 32)
	public DqDate record_creation_date;
	@Field(name="Record Update Date", index = 33)
	public DqDate record_update_date;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}


	@Override
	public String getID() {
		return patID.getValue();
	}
}
