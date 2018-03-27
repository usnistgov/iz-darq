package gov.nist.healthcare.iz.darq.digest.service;

import java.util.Map;

import gov.nist.healthcare.iz.darq.digest.domain.ADChunk;
import gov.nist.healthcare.iz.darq.digest.domain.DetectionSum;
import gov.nist.healthcare.iz.darq.digest.domain.PatientPayload;
import gov.nist.healthcare.iz.darq.digest.domain.TablePayload;
import gov.nist.healthcare.iz.darq.digest.domain.VaccinationPayload;

public interface MergeService {

	ADChunk mergeChunk(ADChunk a, ADChunk b);

	Map<String, Map<String, VaccinationPayload>> mergeVxProvider(Map<String, Map<String, VaccinationPayload>> a,
			Map<String, Map<String, VaccinationPayload>> b);

	Map<String, VaccinationPayload> mergeVxAgeGroup(Map<String, VaccinationPayload> a,
			Map<String, VaccinationPayload> b);

	VaccinationPayload mergeVxPayload(VaccinationPayload a, VaccinationPayload b);

	Map<String, DetectionSum> mergeDets(Map<String, DetectionSum> a, Map<String, DetectionSum> b);

	Map<String, Map<String, Map<String, Map<String, Integer>>>> mergeVxCode(
			Map<String, Map<String, Map<String, Map<String, Integer>>>> a,
			Map<String, Map<String, Map<String, Map<String, Integer>>>> b);

	Map<String, Map<String, Map<String, Integer>>> mergeVxYear(Map<String, Map<String, Map<String, Integer>>> a,
			Map<String, Map<String, Map<String, Integer>>> b);

	Map<String, Map<String, Integer>> mergeVxSource(Map<String, Map<String, Integer>> a,
			Map<String, Map<String, Integer>> b);

	Map<String, Integer> mergeVxSex(Map<String, Integer> a, Map<String, Integer> b);

	Integer merge(Integer a, Integer b);

	Map<String, PatientPayload> mergePatAgeGroup(Map<String, PatientPayload> a, Map<String, PatientPayload> b);

	PatientPayload mergePat(PatientPayload a, PatientPayload b);

	Map<String, TablePayload> mergeCodeTable(Map<String, TablePayload> a, Map<String, TablePayload> b);

	TablePayload mergeCode(TablePayload a, TablePayload b);

}