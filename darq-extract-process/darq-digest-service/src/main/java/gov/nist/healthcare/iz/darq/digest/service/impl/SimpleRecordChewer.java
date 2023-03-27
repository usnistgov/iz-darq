package gov.nist.healthcare.iz.darq.digest.service.impl;

import java.util.HashMap;
import java.util.Map;

import gov.nist.healthcare.iz.darq.digest.domain.*;
import gov.nist.healthcare.iz.darq.digest.service.patient.matching.PatientMatchingService;
import gov.nist.healthcare.iz.darq.patient.matching.model.PatientMatchingDetection;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import gov.nist.healthcare.iz.darq.digest.service.AgeGroupService;
import gov.nist.healthcare.iz.darq.digest.service.ConfigurationProvider;
import gov.nist.healthcare.iz.darq.digest.service.RecordChewer;
import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;

@Service
public class SimpleRecordChewer implements RecordChewer {

	@Autowired
	private SimpleGroupService groupService;
	@Autowired
	private LineItemizer itemizer;

	
	@Override
	public ADChunk munch(ConfigurationProvider configuration, AggregatePatientRecord apr, LocalDate date, PatientMatchingService matchingService) throws Exception {

		if(apr.patient.date_of_birth.getValue().isAfter(date)) {
			throw new Exception("Birth date is after Evaluation Date ");
		}

		AgeGroupService ageGroupCalculator = configuration.ageGroupService();
		DQAValidator validator = new DQAValidator(ageGroupCalculator, configuration.detectionFilter(), configuration.vaxGroupMapper());
		CodeCollector collector = new CodeCollector();
		validator.validateRecord(apr, date);

		try {
			collector.collectPatient(itemizer.itemizePatient(apr.patient), ageGroupCalculator.getGroup(apr.patient.date_of_birth.getValue(), date));
			for(VaccineRecord vr : apr.history){
				collector.collectVaccination(itemizer.itemizeVax(vr), ageGroupCalculator.getGroup(new LocalDate(apr.patient.date_of_birth.getValue()), new LocalDate(vr.administration_date.getValue())), vr.reporting_group.toString());
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}

		Map<String, String> providers = new HashMap<>();
		
		
		Map<String, Map<String, VaccinationPayload>> vaccinationSection = this.groupService.makeVxSectionProvider(configuration.ageGroupService(), validator.getVxInfoTable(), validator.vaccinationDetections(), collector.getVaccinationCodes());
		Map<String, PatientPayload> patientSection = this.groupService.makePatSectionAge(configuration.ageGroupService(), validator.patientDetections(), collector.getPatientCodes());
		Map<String, ExtractFraction> extraction = collector.getExtract();
		Map<String, Map<String, ADPayload>> merged = this.groupService.makeADPayloadMap(patientSection, vaccinationSection);

		if(matchingService != null) {
		  /// TODO Fix This
		  boolean matches = matchingService.process(apr);
		  if(matches) {
			patientSection.values().stream().forEach((payload) -> {
			  payload.getDetection().put(PatientMatchingDetection.PM001.toString(), new DetectionSum(1, 0));
			});
		  } else {
			patientSection.values().stream().forEach((payload) -> {
			  payload.getDetection().put(PatientMatchingDetection.PM001.toString(), new DetectionSum(0, 1));
			});
		  }
		}

		Map<String, Map<String, ADPayload>> deIdentifiedSection = new HashMap<>();
		for(String provider : merged.keySet()){
			String hash = DigestUtils.md5DigestAsHex(provider.getBytes());
			deIdentifiedSection.put(hash, merged.get(provider));
			providers.put(provider, hash);
		}

		return new ADChunk(
				providers,
				patientSection,
				deIdentifiedSection,
				extraction,
				apr.history.size(),
				1,
				validator.getHistorical(),
				validator.getAdministered());
	}

}
