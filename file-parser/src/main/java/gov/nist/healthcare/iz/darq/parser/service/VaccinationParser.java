package gov.nist.healthcare.iz.darq.parser.service;

import gov.nist.healthcare.iz.darq.parser.model.VISInformation;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;
import gov.nist.healthcare.iz.darq.parser.type.DqDate;
import gov.nist.healthcare.iz.darq.parser.type.DqString;
import gov.nist.lightdb.exception.InvalidValueException;
import gov.nist.lightdb.service.Parser;

public class VaccinationParser implements Parser<VaccineRecord> {

	@Override
	public VaccineRecord parse(String line) throws InvalidValueException {
		String[] payloads = line.split("\t");
		assert(payloads.length == 21);
		VaccineRecord vax = new VaccineRecord();
		
		try {
			vax.patID = new DqString(payloads[0]);
		}
		catch(InvalidValueException e) {
			throw new InvalidValueException("ID IS REQUIRED "+e.toString());
		}

		try {
			vax.vaccine_type_cvx = new DqString(payloads[1]);
			vax.vaccine_type_ndc = new DqString(payloads[2]);
			vax.administration_date = new DqDate(payloads[3]);
			vax.manufacturer = new DqString(payloads[4]);
			vax.lot_number = new DqString(payloads[5]);
			vax.event_information_source = new DqString(payloads[6]);
			vax.admin_provider = new DqString(payloads[7]);
			vax.admin_location = new DqString(payloads[8]);
			vax.admin_route = new DqString(payloads[9]);
			vax.admin_site = new DqString(payloads[10]);
			vax.exp_date = new DqDate(payloads[11]);
			vax.volume_unit = new DqString(payloads[12]);
			vax.ordering_provider = PatientParser.readName(payloads, 13); // + 3
			vax.vis = readVIS(payloads, 16); // + 3
			vax.eligibility_at_dose = new DqString(payloads[19]);
			vax.complete_status = new DqString(payloads[20]);
		}
		catch(InvalidValueException e) {
			throw new InvalidValueException("[Record : "+vax.patID.getValue()+"] "+e.toString());
		}
		return vax;
	}
	
	public static VISInformation readVIS(String[] payloads, int i) throws InvalidValueException{
		VISInformation vis = new VISInformation();
		vis.type = new DqString(payloads[i]);
		vis.publication_date = new DqDate(payloads[i + 1]);
		vis.given_date = new DqDate(payloads[i + 2]);
		return vis;
	}
	


}
