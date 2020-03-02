package gov.nist.healthcare.iz.darq.digest.service;

import java.io.IOException;
import java.util.Map;

import gov.nist.healthcare.iz.darq.digest.domain.ADFile;
import gov.nist.healthcare.iz.darq.digest.domain.PatientPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Summary;
import gov.nist.healthcare.iz.darq.digest.domain.VaccinationPayload;

public interface HTMLSummaryGenerator {

	void generateSummary(ADFile file, Summary summary, Map<String, String> providers, String path) throws IOException;
//	void generateADFView(Map<String, Map<String, VaccinationPayload>> vaccinationSection, Map<String, PatientPayload> patientSection, String path) throws IOException;
}
