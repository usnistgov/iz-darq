package gov.nist.healthcare.iz.darq.digest.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.nist.healthcare.iz.darq.detections.RecordDetectionEngineResult;
import gov.nist.healthcare.iz.darq.digest.domain.ExtractFraction;
import gov.nist.healthcare.iz.darq.adf.service.MergeService;
import gov.nist.healthcare.iz.darq.detections.AggregatedRecordDetections;
import gov.nist.healthcare.iz.darq.detections.DetectionContext;
import gov.nist.healthcare.iz.darq.detections.DetectionEngine;
import gov.nist.healthcare.iz.darq.digest.domain.DetectionSum;
import gov.nist.healthcare.iz.darq.digest.domain.*;
import gov.nist.healthcare.iz.darq.digest.service.report.ReportEngine;
import gov.nist.healthcare.iz.darq.digest.service.vocabulary.RecordValuesAnalysisResult;
import gov.nist.healthcare.iz.darq.digest.service.vocabulary.SimpleRecordValueAnalysisService;
import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import gov.nist.healthcare.iz.darq.digest.service.RecordChewer;

@Service
public class SimpleRecordChewer implements RecordChewer {

	@Autowired
	private SimpleGroupService groupService;
	@Autowired
	private SimpleRecordValueAnalysisService recordValueAnalysisService;
	@Autowired
	private MergeService mergeService;
	@Autowired
	DetectionEngine detectionEngine;
	@Autowired
	ReportEngine reportEngine;

	@Override
	public ADChunk munch(PreProcessRecord record, LocalDate date, DetectionContext detectionContext) throws Exception {

		if(record.getRecord().patient.date_of_birth.getValue().isAfter(date)) {
			throw new Exception("Birth date is after Evaluation Date ");
		}

		// Analyze Record (Detections & Vocabulary & Vaccination Events)
		RecordDetectionEngineResult recordDetectionEngineResult = detectionEngine.processRecordAndGetDetections(record, detectionContext);
		AggregatedRecordDetections detections = aggregate(recordDetectionEngineResult, record);
		RecordValuesAnalysisResult recordValuesAnalysisResult = recordValueAnalysisService.analyseRecordValues(record);
		Map<String, Map<String, Map<String, Map<String, Map<String, TablePayload>>>>> aggregateVaccinationEvents = recordValueAnalysisService.getVaccinationEvents(record, detectionContext);

		// Count Administered & Historical
		int administered = (int) record.getRecord().history.stream().filter((vx) -> "00".equals(vx.event_information_source.getValue())).count();
		int historical = record.getRecord().history.size() - administered;

		// Create ADF Sections
		Map<String, Map<String, VaccinationPayload>> vaccinationSection = makeVaccinationSection(detections, recordValuesAnalysisResult, aggregateVaccinationEvents);
		Map<String, PatientPayload> patientSection = makePatientSection(detections, recordValuesAnalysisResult, record.getPatientAgeGroup());
		Map<String, ExtractFraction> extraction = recordValuesAnalysisResult.getFieldsExtraction();
		Map<String, Map<String, ADPayload>> merged = this.groupService.makeADPayloadMap(patientSection, vaccinationSection);

		// Obfuscate Reporting Group
		Map<String, Map<String, ADPayload>> deIdentifiedSection = new HashMap<>();
		Map<String, String> providers = new HashMap<>();
		for(String provider : merged.keySet()){
			String hash = detectionContext.obfuscateReportingGroup(provider);
			deIdentifiedSection.put(hash, merged.get(provider));
			providers.put(provider, hash);
		}

		reportEngine.process(record, recordDetectionEngineResult);

		// Create ADF Chunk
		return new ADChunk(
				providers,
				patientSection,
				deIdentifiedSection,
				extraction,
				detections.getRecordMatchSignatures(),
				record.getRecord().history.size(),
				1,
				historical,
				administered
		);
	}

	Map<String, Map<String, VaccinationPayload>> makeVaccinationSection(AggregatedRecordDetections detections, RecordValuesAnalysisResult recordValuesAnalysisResult, Map<String, Map<String, Map<String, Map<String, Map<String, TablePayload>>>>> vaccinationEvents) {
		Map<String, Map<String, VaccinationPayload>> vaccinationSection = new HashMap<>();
		Map<String, Map<String, Map<String, DetectionSum>>> vaccinationDetections = detections.getVaccinations();
		Map<String, Map<String, Map<String, TablePayload>>> vaccinationCodes = recordValuesAnalysisResult.getVaccinationCodes();

		getKeys(vaccinationDetections, vaccinationCodes, vaccinationEvents).forEach((provider) -> {
			Map<String, Map<String, DetectionSum>> detectionsByAgeGroup = vaccinationDetections.getOrDefault(provider, new HashMap<>());
			Map<String, Map<String, TablePayload>> codesByAgeGroup = vaccinationCodes.getOrDefault(provider, new HashMap<>());
			Map<String, Map<String, Map<String, Map<String, TablePayload>>>> eventsByAgeGroup = vaccinationEvents.getOrDefault(provider, new HashMap<>());

			Map<String, VaccinationPayload> sectionByAgeGroup = vaccinationSection.computeIfAbsent(provider, (k) -> new HashMap<>());

			getKeys(detectionsByAgeGroup, codesByAgeGroup, eventsByAgeGroup).forEach((ageGroup) -> {
				Map<String, DetectionSum> detectionsPayload = detectionsByAgeGroup.getOrDefault(ageGroup, new HashMap<>());
				Map<String, TablePayload> codesPayload = codesByAgeGroup.getOrDefault(ageGroup, new HashMap<>());
				Map<String, Map<String, Map<String, TablePayload>>> eventsPayload = eventsByAgeGroup.getOrDefault(ageGroup, new HashMap<>());

				if(sectionByAgeGroup.containsKey(ageGroup)) {
					VaccinationPayload payload = sectionByAgeGroup.get(ageGroup);
					VaccinationPayload merged = this.mergeService.mergeVxPayload(new VaccinationPayload(detectionsPayload, codesPayload, eventsPayload), payload);
					sectionByAgeGroup.put(ageGroup, merged);
				} else {
					sectionByAgeGroup.put(ageGroup, new VaccinationPayload(detectionsPayload, codesPayload, eventsPayload));
				}
			});
		});

		return vaccinationSection;
	}

	Map<String, PatientPayload> makePatientSection(AggregatedRecordDetections detections, RecordValuesAnalysisResult recordValuesAnalysisResult, String ageGroup) {
		Map<String, PatientPayload> patientSection = new HashMap<>();
		Map<String, DetectionSum> patientDetections = detections.getPatient();
		Map<String, TablePayload> patientCodes = recordValuesAnalysisResult.getPatientCodes();

		PatientPayload payload = new PatientPayload(patientDetections, patientCodes, 1);
		patientSection.put(ageGroup, payload);
		return patientSection;
	}


	public AggregatedRecordDetections aggregate(RecordDetectionEngineResult detections, PreProcessRecord record) {
		AggregatedRecordDetections aggregated = new AggregatedRecordDetections();
		aggregated.setPatient(detections.getPatientDetections());
		Map<String, Map<String, Map<String, DetectionSum>>> vaccinationsAggDetections = new HashMap<>();
		aggregated.setVaccinations(vaccinationsAggDetections);
		if(detections.getVaccinationDetectionsById() != null) {
			for(String vaccinationId: detections.getVaccinationDetectionsById().keySet()) {
				Map<String, DetectionSum> vaccinationDetections = detections.getVaccinationDetectionsById().get(vaccinationId);
				String provider = record.getProvidersByVaccinationId().get(vaccinationId);
				String ageGroup = record.getAgeGroupAtVaccinationByVaccinationId().get(vaccinationId);

				if(!vaccinationsAggDetections.containsKey(provider)) {
					vaccinationsAggDetections.put(provider, new HashMap<>());
				}

				if(!vaccinationsAggDetections.get(provider).containsKey(ageGroup)) {
					vaccinationsAggDetections.get(provider).put(ageGroup, new HashMap<>());
				}

				vaccinationDetections.forEach((k, v) -> {
					vaccinationsAggDetections.get(provider).get(ageGroup).merge(k, v, DetectionSum::merge);
				});
			}
		}
		Map<String, Integer> signatures = new HashMap<>();
		for(String signature: detections.getPossiblePatientRecordDuplicatesWithSignature().values()) {
			signatures.compute(signature, (k, v) -> v == null ? 1 : v + 1);
		}
		aggregated.setRecordMatchSignatures(signatures);
		aggregated.setDuplicates(detections.getPossiblePatientRecordDuplicatesWithSignature());
		return aggregated;
	}

	@SafeVarargs
	final Set<String> getKeys(Map<String, ?>... maps) {
		Set<String> keys = new HashSet<>();
		for(Map<String, ?> map: maps) {
			keys.addAll(map.keySet());
		}
		return keys;
	}




}
