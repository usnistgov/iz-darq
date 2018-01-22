package gov.nist.healthcare.iz.darq.analysis.dqa;

import java.util.ArrayList;
import java.util.List;

import org.immregistries.dqa.validator.engine.MessageValidator;
import org.immregistries.dqa.validator.engine.ValidationRuleResult;
import org.immregistries.dqa.validator.issue.ValidationIssue;
import org.immregistries.dqa.vxu.DqaMessageReceived;
import org.immregistries.dqa.vxu.DqaNextOfKin;
import org.immregistries.dqa.vxu.DqaPatient;
import org.immregistries.dqa.vxu.DqaVaccination;
import org.immregistries.dqa.vxu.VaccinationVIS;
import org.immregistries.dqa.vxu.hl7.PatientAddress;
import org.immregistries.dqa.vxu.transform.MessageTransformer;

import gov.nist.healthcare.iz.darq.analysis.Detection;
import gov.nist.healthcare.iz.darq.analysis.service.DataQualityCheck;
import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;


public class DQACheck implements DataQualityCheck {

	private MessageValidator validator = MessageValidator.INSTANCE;
	private MessageTransformer transformer = MessageTransformer.INSTANCE;
	
	// Birth Facility == Birth Place ?
	// Mother Name ?
	// IIS Level ?
	// Provider Facility Level ?
	// Email Address ?
	
	// Vax
	// Admin Provider First - Last - Middle / One String
	// Ordering Provider Middle ?
	// Elegibility at dose ?
	// VIS Type == Document Code ?
	// Published Date format ?
	// VIS Given date ?
	
	@Override
	public List<Detection> inspect(AggregatePatientRecord record) {
		DqaMessageReceived recordAsMessageStruct = new DqaMessageReceived();
		
		DqaPatient patient = new DqaPatient();
		patient.setNameFirst(record.patient.own_name.first.getValue());
		patient.setNameLast(record.patient.own_name.last.getValue());
		patient.setNameMiddle(record.patient.own_name.middle.getValue());
		patient.setAliasFirst(record.patient.alias_name.first.getValue());
		patient.setAliasLast(record.patient.alias_name.last.getValue());
		patient.setAliasMiddle(record.patient.alias_name.middle.getValue());
		patient.setMotherMaidenName(record.patient.mother_maiden_name.getValue());
		patient.setBirthDate(record.patient.date_of_birth.getValue());
		patient.setSexCode(record.patient.gender.getValue());
		patient.setBirthMultipleIndicator(record.patient.multi_birth_indicator.getValue());
		patient.setBirthOrderNumber(record.patient.birth_order.getValue()+"");
		patient.setPrimaryLanguageCode(record.patient.language.getValue());
		patient.setPhoneNumber(record.patient.phone.getValue());
		patient.setEthnicityCode(record.patient.ethnicity_codes.getValue()+"");
		patient.setRaceCode(record.patient.race_codes.getValue()+"");
		patient.setBirthPlace(record.patient.birth_facility_name.getValue());
		
		PatientAddress address = new PatientAddress();
		
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
		
		recordAsMessageStruct.getNextOfKins().add(nk);
		recordAsMessageStruct.setPatient(patient);
		
		for(VaccineRecord vr : record.history){
			DqaVaccination vax = new DqaVaccination();
			vax.setAdminCvxCode(vr.vaccine_type_cvx.getValue());
			vax.setAdminNdcCode(vr.vaccine_type_ndc.getValue());
			vax.setAdminDate(vr.administration_date.getValue());
			vax.setManufacturerCode(vr.manufacturer.getValue());
			vax.setLotNumber(vr.lot_number.getValue());
			vax.setBodySiteCode(vr.admin_site.getValue());
			vax.setBodyRouteCode(vr.admin_route.getValue());
			vax.setExpirationDate(vr.exp_date.getValue());
			vax.setAmountUnitCode(vr.volume_unit.getValue());
			vax.setOrderedByNameFirst(vr.ordering_provider.first.getValue());
			vax.setOrderedByNameLast(vr.ordering_provider.last.getValue());
			vax.setCompletionCode(vr.complete_status.getValue());
			vax.setGivenByNameFirst(vr.admin_provider.getValue());
			vax.setFacilityName(vr.admin_location.getValue());
			vax.setInformationSource(vr.event_information_source.getValue());
			VaccinationVIS vis = new VaccinationVIS();
			vis.setDocumentCode(vr.vis.type.getValue());
			vis.setPublishedDateString(vr.vis.publication_date.getValue() == null ? "" : vr.vis.publication_date.getValue().toString());
			
			vax.setVaccinationVis(vis);
			recordAsMessageStruct.getVaccinations().add(vax);
		}
		
		transformer.transform(recordAsMessageStruct);
		List<ValidationRuleResult> validationResults = validator.validateMessageNIST(recordAsMessageStruct);
		List<Detection> detections = new ArrayList<>();
		for(ValidationRuleResult vrr : validationResults){
			for(ValidationIssue iss : vrr.getIssues()){
				detections.add(new Detection(record.ID, iss.getIssue().getDqaErrorCode(), iss.getMessage()));
			}
		}
		
		return detections;
	}

}
