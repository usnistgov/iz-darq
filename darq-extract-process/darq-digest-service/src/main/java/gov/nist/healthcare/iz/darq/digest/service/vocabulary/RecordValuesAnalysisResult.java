package gov.nist.healthcare.iz.darq.digest.service.vocabulary;

import gov.nist.healthcare.iz.darq.digest.domain.ExtractFraction;
import gov.nist.healthcare.iz.darq.digest.domain.TablePayload;

import java.util.Map;

public class RecordValuesAnalysisResult {
	Map<String, ExtractFraction> fieldsExtraction;
	// Table, Code, Count
	Map<String, TablePayload> patientCodes;
	// Provider, Age Group, Table, Code, Count
	Map<String, Map<String, Map<String, TablePayload>>> vaccinationCodes;

	public RecordValuesAnalysisResult(Map<String, ExtractFraction> fieldsExtraction, Map<String, TablePayload> patientCodes, Map<String, Map<String, Map<String, TablePayload>>> vaccinationCodes) {
		this.fieldsExtraction = fieldsExtraction;
		this.patientCodes = patientCodes;
		this.vaccinationCodes = vaccinationCodes;
	}

	public Map<String, ExtractFraction> getFieldsExtraction() {
		return fieldsExtraction;
	}

	public Map<String, TablePayload> getPatientCodes() {
		return patientCodes;
	}

	public Map<String, Map<String, Map<String, TablePayload>>> getVaccinationCodes() {
		return vaccinationCodes;
	}

	public void setFieldsExtraction(Map<String, ExtractFraction> fieldsExtraction) {
		this.fieldsExtraction = fieldsExtraction;
	}

	public void setPatientCodes(Map<String, TablePayload> patientCodes) {
		this.patientCodes = patientCodes;
	}

	public void setVaccinationCodes(Map<String, Map<String, Map<String, TablePayload>>> vaccinationCodes) {
		this.vaccinationCodes = vaccinationCodes;
	}
}
