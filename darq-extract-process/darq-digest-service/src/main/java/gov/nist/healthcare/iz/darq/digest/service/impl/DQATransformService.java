package gov.nist.healthcare.iz.darq.digest.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.immregistries.dqa.validator.transform.MessageTransformer;
import org.immregistries.dqa.vxu.DqaMessageReceived;
import org.immregistries.dqa.vxu.DqaNextOfKin;
import org.immregistries.dqa.vxu.DqaPatient;
import org.immregistries.dqa.vxu.DqaPatientAddress;
import org.immregistries.dqa.vxu.DqaVaccination;
import org.immregistries.dqa.vxu.VaccinationVIS;

import gov.nist.healthcare.iz.darq.digest.service.TransformService;
import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;
import gov.nist.healthcare.iz.darq.parser.model.Name;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;

public class DQATransformService implements TransformService<DqaMessageReceived> {
	
	private MessageTransformer transformer = MessageTransformer.INSTANCE;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	@Override
	public DqaMessageReceived transform(AggregatePatientRecord record) {
		DqaMessageReceived recordAsMessageStruct = new DqaMessageReceived();
		
		
		DqaPatient patient = new DqaPatient();
		patient.setIdRegistryNumber(record.ID);
		patient.setNameFirst(record.patient.own_name.first.getValue());
		patient.setNameLast(record.patient.own_name.last.getValue());
		patient.setNameMiddle(record.patient.own_name.middle.getValue());
		patient.setAliasFirst(record.patient.alias_name.first.getValue());
		patient.setAliasLast(record.patient.alias_name.last.getValue());
		patient.setAliasMiddle(record.patient.alias_name.middle.getValue());
		patient.setMotherMaidenName(record.patient.mother_maiden_name.getValue());
		patient.setBirthDateString(formatDate(record.patient.date_of_birth.getValue()));
		patient.setSexCode(record.patient.gender.getValue());
		patient.setBirthMultipleIndicator(record.patient.multi_birth_indicator.getValue());
		patient.setBirthOrderNumber(record.patient.birth_order.getValue()+"");
		patient.setPrimaryLanguageCode(record.patient.language.getValue());
		patient.setPhoneNumber(record.patient.phone.getValue());
		patient.setEthnicityCode(record.patient.ethnicity_code.getValue()+"");
		patient.setRaceCode(record.patient.race_code.getValue()+"");
		patient.setBirthPlace(record.patient.birth_facility_name.getValue());
		patient.setEmailAddress(record.patient.email_address.getValue());
		patient.setBirthPlace(record.patient.birth_facility_name.getValue());
		patient.setRegistryStatusCode(record.patient.provider_facility_level.getValue());
		patient.setRegistryStatusUniversal(record.patient.iis_level.getValue());
		
		DqaPatientAddress address = new DqaPatientAddress();
		
		address.setStreet(record.patient.address.street.getValue());
		address.setCity(record.patient.address.city.getValue());
		address.setCountryCode(record.patient.address.country.getValue());
		address.setZip(record.patient.address.zip.getValue());
		address.setStateCode(record.patient.address.state.getValue());
		
		patient.getPatientAddressList().add(address);
		
		DqaNextOfKin nk = new DqaNextOfKin();
		nk.setNameFirst(record.patient.responsible_party.name.first.getValue());
		nk.setNameLast(record.patient.responsible_party.name.last.getValue());
		nk.setNameMiddle(record.patient.responsible_party.name.middle.getValue());
		nk.setRelationshipCode(record.patient.responsible_party.relationshipToPatient.getValue());
		
		DqaNextOfKin mnk = this.motherAsNK1(nk, record.patient.mother_name);
		
		recordAsMessageStruct.getNextOfKins().add(nk);
		if(mnk != null){
			recordAsMessageStruct.getNextOfKins().add(mnk);
		}
		recordAsMessageStruct.setPatient(patient);
		
		int i = 0;
		for(VaccineRecord vr : record.history){
			DqaVaccination vax = new DqaVaccination(i+"id");
			i++;
			vax.setAdminCvxCode(vr.vaccine_type_cvx.getValue());
			vax.setAdminNdcCode(vr.vaccine_type_ndc.getValue());
			vax.setAdminDateString(formatDate(vr.administration_date.getValue()));
			vax.setManufacturerCode(vr.manufacturer.getValue());
			vax.setLotNumber(vr.lot_number.getValue());
			
			vax.setBodySiteCode(vr.admin_site.getValue());
			vax.setBodyRouteCode(vr.admin_route.getValue());
			
			vax.setExpirationDate(vr.exp_date.getValue());
			vax.setAmount(vr.volume_unit.getValue());
			
			vax.setOrderedByNameFirst(vr.ordering_provider.first.getValue());
			vax.setOrderedByNameLast(vr.ordering_provider.last.getValue());
			
			vax.setCompletionCode(vr.complete_status.getValue());
			
			// Change 
			vax.setGivenByNameFirst(vr.admin_provider.getValue());
			
			//Change
			vax.setFacilityName(vr.admin_location.getValue());
			
			vax.setInformationSource(vr.event_information_source.getValue());
			vax.setFinancialEligibilityCode(vr.eligibility_at_dose.getValue());
			
			VaccinationVIS vis = new VaccinationVIS();

			vis.setCvxCode(vr.vis.type.getValue());
			vis.setPublishedDateString(formatDate(vr.vis.publication_date.getValue()));
			vis.setPresentedDateString(formatDate(vr.vis.given_date.getValue()));
			
			vax.setVaccinationVis(vis);
			recordAsMessageStruct.getVaccinations().add(vax);
		}
		
		transformer.transform(recordAsMessageStruct);
		return recordAsMessageStruct;
	}
	
	private DqaNextOfKin motherAsNK1(DqaNextOfKin nk1, Name motherName){
		if(nk1.getNameFirst().equals(motherName.first.getValue()) && nk1.getNameLast().equals(motherName.last.getValue()) && nk1.getNameMiddle().equals(motherName.middle.getValue()) && nk1.getRelationshipCode().equals("MTH")){
			return null;
		}
		else {
			DqaNextOfKin nk = new DqaNextOfKin();
			nk.setNameFirst(motherName.first.getValue());
			nk.setNameLast(motherName.last.getValue());
			nk.setNameMiddle(motherName.middle.getValue());
			nk.setRelationshipCode("MTH");
			return nk;
		}
	}
	
	private String formatDate(Date date){
		if(date != null) return dateFormat.format(date);
		return "";
	}

}
