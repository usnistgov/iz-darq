package gov.nist.healthcare.iz.darq.digest.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import gov.nist.healthcare.iz.darq.adf.model.Metadata;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFWriter;
import gov.nist.healthcare.iz.darq.digest.domain.Summary;

public interface HTMLSummaryGenerator {

	void generateSummary(ADFWriter writer, Metadata metadata, Summary summary, List<String> issues, Map<String, String> providers, String path, boolean printAdf) throws IOException, Exception;

}
