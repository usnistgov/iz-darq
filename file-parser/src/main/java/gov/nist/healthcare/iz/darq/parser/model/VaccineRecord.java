package gov.nist.healthcare.iz.darq.parser.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import gov.nist.healthcare.iz.darq.parser.type.DqDate;
import gov.nist.healthcare.iz.darq.parser.type.DqString;
import gov.nist.lightdb.domain.Record;

public class VaccineRecord extends Record {
	
	public DqString patID;
	public DqString vaccine_type_cvx;
	public DqString vaccine_type_ndc;
	public DqDate administration_date;
	public DqString manufacturer;
	public DqString lot_number;
	public DqString event_information_source;
	public DqString admin_provider;
	public DqString admin_location;
	public DqString admin_route;
	public DqString admin_site;
	public DqDate exp_date;
	public DqString volume_unit;
	public Name ordering_provider;
	public VISInformation vis;
	public DqString eligibility_at_dose;
	public DqString complete_status;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
