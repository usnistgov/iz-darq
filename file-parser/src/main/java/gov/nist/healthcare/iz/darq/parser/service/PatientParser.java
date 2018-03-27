package gov.nist.healthcare.iz.darq.parser.service;

import gov.nist.healthcare.iz.darq.parser.model.Address;
import gov.nist.healthcare.iz.darq.parser.model.Name;
import gov.nist.healthcare.iz.darq.parser.model.Patient;
import gov.nist.healthcare.iz.darq.parser.model.ResponsibleParty;
import gov.nist.healthcare.iz.darq.parser.model.VISInformation;
import gov.nist.healthcare.iz.darq.parser.type.DqDate;
import gov.nist.healthcare.iz.darq.parser.type.DqNumeric;
import gov.nist.healthcare.iz.darq.parser.type.DqString;
import gov.nist.lightdb.exception.InvalidValueException;
import gov.nist.lightdb.service.Parser;

public class PatientParser implements Parser<Patient> {

	@Override
	public Patient parse(String line) throws InvalidValueException  {
		String[] payloads = line.split("\t");
		assert(payloads.length == 32);
			
		Patient pat = new Patient();
		
		try {
			pat.patID = new DqString(payloads[0]);
			if(!pat.patID.hasValue()) throw new InvalidValueException("No ID was defined for line : "+line);
		}
		catch(InvalidValueException e) {
			throw new InvalidValueException("ID IS REQUIRED "+e.toString());
		}
		
		try {
			pat.own_name = readName(payloads, 1); // + 3
			pat.mother_maiden_name = new DqString(payloads[4]);
			pat.mother_name = readName(payloads, 5); // + 3
			pat.date_of_birth = new DqDate(payloads[8]);
			pat.gender = new DqString(payloads[9]);
			pat.address = readAddress(payloads, 10); // + 5
			pat.race_code = new DqNumeric(payloads[15]);
			pat.ethnicity_code = new DqNumeric(payloads[16]);
			pat.phone =  new DqString(payloads[17]);
			pat.email_address = new DqString(payloads[18]);
			pat.language = new DqString(payloads[19]);
			pat.alias_name = readName(payloads, 20); // + 3
			pat.responsible_party = readResponsibleParty(payloads, 23); // + 4
			pat.birth_facility_name = new DqString(payloads[27]);
			pat.multi_birth_indicator = new DqString(payloads[28]);
			pat.birth_order = new DqNumeric(payloads[29]);
			pat.provider_facility_level = new DqString(payloads[30]);
			pat.iis_level = new DqString(payloads[31]);
		}
		catch(InvalidValueException e) {
			throw new InvalidValueException("[Record : "+pat.patID.getValue()+"] "+e.toString());
		}
		
		return pat;
	}

	
	public static Name readName(String[] payloads, int i) throws InvalidValueException{
		Name name = new Name();
		name.first = new DqString(payloads[i]);
		name.middle = new DqString(payloads[i+1]);
		name.last = new DqString(payloads[i+2]);
		
		return name;
	}
	
	public static Address readAddress(String[] payloads, int i) throws InvalidValueException{
		Address addr = new Address();
		addr.street = new DqString(payloads[i]);
		addr.city = new DqString(payloads[i+1]);
		addr.state = new DqString(payloads[i+2]);
		addr.country = new DqString(payloads[i+3]);;
		addr.zip = new DqString(payloads[i+4]);
		return addr;
	}
	
	public static ResponsibleParty readResponsibleParty(String[] payloads, int i) throws InvalidValueException{
		ResponsibleParty rsp = new ResponsibleParty();
		rsp.name = readName(payloads, i); // + 3
		rsp.relationshipToPatient = new DqString(payloads[i + 3]);
		return rsp;
	}
	
	public static VISInformation readVIS(String[] payloads, int i) throws InvalidValueException{
		VISInformation vis = new VISInformation();
		vis.type = new DqString(payloads[i]);
		vis.publication_date = new DqDate(payloads[i + 1]);
		vis.given_date = new DqDate(payloads[i + 2]);
		return vis;
	}

}
