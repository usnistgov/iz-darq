package gov.nist.healthcare.iz.darq.parser.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import gov.nist.healthcare.iz.darq.parser.type.DqDate;
import gov.nist.healthcare.iz.darq.parser.type.DqString;
import gov.nist.lightdb.domain.Record;

public class VaccineRecord extends Record {
	
	@FieldName("Patient ID")
	public DqString patID;
	@FieldName("Vaccine Type CVX")
	public DqString vaccine_type_cvx;
	@FieldName("Vaccine Type NDC")
	public DqString vaccine_type_ndc;
	@FieldName("Administration Date")
	public DqDate administration_date;
	@FieldName("Vaccine Manufacturer")
	public DqString manufacturer;
	@FieldName("Vaccine Lot Number")
	public DqString lot_number;
	@FieldName("Event Information Source")
	public DqString event_information_source;
	@FieldName("Administring Provider")
	public DqString admin_provider;
	@FieldName("Administred at Location")
	public DqString admin_location;
	@FieldName("Administred Body Route")
	public DqString admin_route;
	@FieldName("Administred Body Site")
	public DqString admin_site;
	@FieldName("Expiration Date")
	public DqDate exp_date;
	@FieldName("Administered Volume (mL)")
	public DqString volume_unit;
	@FieldName("Ordering Provider Name")
	public Name ordering_provider;
	@FieldName("Vaccine VIS")
	public VISInformation vis;
	@FieldName("Eligibility at Dose")
	public DqString eligibility_at_dose;
	@FieldName("Complete Status")
	public DqString complete_status;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public String getID() {
		return patID.getValue();
	}
}
