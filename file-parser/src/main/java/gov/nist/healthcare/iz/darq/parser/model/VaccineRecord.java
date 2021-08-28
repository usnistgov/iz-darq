package gov.nist.healthcare.iz.darq.parser.model;

import gov.nist.healthcare.iz.darq.parser.annotation.Field;
import org.apache.commons.lang3.builder.ToStringBuilder;

import gov.nist.healthcare.iz.darq.parser.type.DqDate;
import gov.nist.healthcare.iz.darq.parser.type.DqString;

@gov.nist.healthcare.iz.darq.parser.annotation.Record(size = 26, name = "Vaccination Record")
public class VaccineRecord extends gov.nist.healthcare.iz.darq.parser.service.model.Record {

	@Field(name="Patient ID", index = 0, required = true)
	public DqString patID;
	@Field(name="Vaccination Event ID", index = 1, required = true)
	public DqString vax_event_id;
	@Field(name="Reporting Group", index = 2, required = true)
	public DqString reporting_group;
	@Field(name="Sending Organization",index = 3)
	public DqString sending_organization;
	@Field(name="Responsible Organization",index = 4)
	public DqString responsible_organization;
	@Field(name="Vaccine Type CVX",index = 7, coded = true, table = "CVX")
	public DqString vaccine_type_cvx;
	@Field(name="Vaccine Type NDC",index = 8, coded = true, table = "NDC")
	public DqString vaccine_type_ndc;
	@Field(name="Administration Date",index = 9)
	public DqDate administration_date;
	@Field(name="Vaccine Manufacturer",index = 10, coded = true, table = "MVX")
	public DqString manufacturer;
	@Field(name="Vaccine Lot Number",index = 11)
	public DqString lot_number;
	@Field(name="Event Information Source",index = 12, coded = true, table = "EVENT_NIP001")
	public DqString event_information_source;
	@Field(name="Administering Provider",index = 6)
	public DqString admin_provider;
	@Field(name="Administered at Location",index = 5)
	public DqString admin_location;
	@Field(name="Administered Body Route",index = 13, coded = true, table = "BODY_ROUTE_0162_NCIT")
	public DqString admin_route;
	@Field(name="Administered Body Site",index = 14, coded = true, table = "BODY_SITE_0163")
	public DqString admin_site;
	@Field(name="Expiration Date",index = 15)
	public DqDate exp_date;
	@Field(name="Administered Volume (mL)",index = 16)
	public DqString volume_unit;
	@Field(name="Ordering Provider Name",index = 17)
	public DqString ordering_provider;
	@Field(name="Vaccine VIS",index = 21)
	public VISInformation vis;
	@Field(name="Eligibility at Dose",index = 19, coded = true, table = "ELIG_0064")
	public DqString eligibility_at_dose;
	@Field(name="Complete Status",index = 18, coded = true, table = "COMP_STATUS_0322")
	public DqString complete_status;
	@Field(name="Vaccine Funding Source",index = 20, coded = true, table = "FUNDING_PHVS_ImmunizationFundingSource_IIS")
	public DqString vaccine_funding_source;
	@Field(name="Record Creation Date",index = 24)
	public DqDate record_creation_date;
	@Field(name="Record Update Date",index = 25)
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
