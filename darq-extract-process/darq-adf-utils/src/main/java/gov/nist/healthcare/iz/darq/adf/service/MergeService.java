package gov.nist.healthcare.iz.darq.adf.service;

import java.util.Map;

import gov.nist.healthcare.iz.darq.digest.domain.*;

public interface MergeService {

	ADChunk mergeChunk(ADChunk a, ADChunk b);


	Map<String, Map<String, ADPayload>> mergeADPayloadProvider(Map<String, Map<String, ADPayload>> a, Map<String, Map<String, ADPayload>> b);

	Map<String, ADPayload> mergeADPayloadAgeGroup(Map<String, ADPayload> a, Map<String, ADPayload> b);

    ADPayload mergeADPayload(ADPayload a, ADPayload b);

    VaccinationPayload mergeVxPayload(VaccinationPayload a, VaccinationPayload b);

	Map<String, DetectionSum> mergeDetections(Map<String, DetectionSum> a, Map<String, DetectionSum> b);

	Map<String, Map<String, Map<String, TablePayload>>> mergeVxYear(
			Map<String, Map<String, Map<String, TablePayload>>> a,
			Map<String, Map<String, Map<String, TablePayload>>> b);

	Map<String, Map<String, TablePayload>> mergeVxGender(Map<String, Map<String, TablePayload>> a,
			Map<String, Map<String, TablePayload>> b);

	Integer merge(Integer a, Integer b);

	Map<String, PatientPayload> mergePatientAgeGroup(Map<String, PatientPayload> a, Map<String, PatientPayload> b);

	PatientPayload mergePatient(PatientPayload a, PatientPayload b);

	Map<String, TablePayload> mergeCodeTable(Map<String, TablePayload> a, Map<String, TablePayload> b);

	TablePayload mergeCode(TablePayload a, TablePayload b);

}