package gov.nist.healthcare.iz.darq.digest.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.immregistries.mqe.validator.transform.MessageTransformer;
import org.immregistries.mqe.vxu.*;
import gov.nist.healthcare.iz.darq.digest.domain.TransformResult;
import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;
import gov.nist.healthcare.iz.darq.parser.model.Name;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;

public class MQETransformService {
	
	private MessageTransformer transformer = MessageTransformer.INSTANCE;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	
	public MQETransformService() {

	}

	public TransformResult<VaccineRecord, MqeVaccination, MqeMessageReceived> transform(AggregatePatientRecord record) {
		MqeMessageReceived recordAsMessageStruct = new MqeMessageReceived();
		TransformResult<VaccineRecord, MqeVaccination, MqeMessageReceived> tr = new TransformResult<>();
		
		MqePatient patient = new MqePatient();
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
		if(record.patient.birth_order.getValue() != null) {
			patient.setBirthOrder(record.patient.birth_order.getValue()+"");
		}
		patient.setPrimaryLanguageCode(record.patient.language.getValue());
		patient.setPhone(new MqePhoneNumber(record.patient.phone.getValue()));
		patient.setEthnicityCode(record.patient.ethnicity_code.getValue());
		patient.setRaceCode(record.patient.race_code.getValue());
		patient.setBirthPlace(record.patient.birth_facility_name.getValue());
		patient.setEmail(record.patient.email_address.getValue());
		patient.setBirthPlace(record.patient.birth_facility_name.getValue());
		patient.setRegistryStatusCode(record.patient.provider_facility_level.getValue());
		patient.setRegistryStatusUniversal(record.patient.iis_level.getValue());
		patient.setSystemEntryDateString(formatDate(record.patient.record_creation_date.getValue()));
		
		MqeAddress address = new MqeAddress();
		
		address.setStreet(record.patient.address.street.getValue());
		address.setCity(record.patient.address.city.getValue());
		address.setCountryCode(record.patient.address.country.getValue());
		address.setZip(record.patient.address.zip.getValue());
		address.setStateCode(record.patient.address.state.getValue());
		
		patient.getPatientAddressList().add(address);
		
		MqeNextOfKin nk = new MqeNextOfKin();
		nk.setNameFirst(record.patient.responsible_party.name.first.getValue());
		nk.setNameLast(record.patient.responsible_party.name.last.getValue());
		nk.setNameMiddle(record.patient.responsible_party.name.middle.getValue());
		nk.setRelationshipCode(record.patient.responsible_party.relationshipToPatient.getValue());
		
		MqeNextOfKin mnk = this.motherAsNK1(nk, record.patient.mother_name);
		
		recordAsMessageStruct.getNextOfKins().add(nk);
		if(mnk != null){
			recordAsMessageStruct.getNextOfKins().add(mnk);
		}
		patient.setResponsibleParty(nk);
		recordAsMessageStruct.setPatient(patient);
		
		int i = 0;
		for(VaccineRecord vr : record.history){
			MqeVaccination vax = new MqeVaccination(i+"id");
			vax.setPositionId(i);
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
			
			vax.setOrderedByNumber(vr.ordering_provider.getValue());
			vax.setCompletionCode(vr.complete_status.getValue());
			
			// Change 
			vax.setGivenByNameFirst(vr.admin_provider.getValue());
			
			//Change
			vax.setFacilityName(vr.admin_location.getValue());
			vax.setInformationSource(vr.event_information_source.getValue());
			vax.setFinancialEligibilityCode(vr.eligibility_at_dose.getValue());
			vax.setFundingSourceCode(vr.vaccine_funding_source.getValue());
			vax.setSystemEntryDateString(formatDate(vr.record_creation_date.getValue()));
			
			VaccinationVIS vis = new VaccinationVIS();

			vis.setCvxCode(vr.vis.type.getValue());
			vis.setPublishedDateString(formatDate(vr.vis.publication_date.getValue()));
			vis.setPresentedDateString(formatDate(vr.vis.given_date.getValue()));
			
			vax.setVaccinationVis(vis);
			tr.add(vr.vax_event_id.getValue(), vax.getPositionId() + "", vr, vax);
			recordAsMessageStruct.getVaccinations().add(vax);
		}

		transformer.transform(recordAsMessageStruct);
		tr.setPayload(recordAsMessageStruct);
		return tr;
	}
	
	private MqeNextOfKin motherAsNK1(MqeNextOfKin nk1, Name motherName){
		if(nk1.getNameFirst().equals(motherName.first.getValue()) && nk1.getNameLast().equals(motherName.last.getValue()) && nk1.getNameMiddle().equals(motherName.middle.getValue()) && nk1.getRelationshipCode().equals("MTH")){
			return null;
		}
		else {
			MqeNextOfKin nk = new MqeNextOfKin();
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
