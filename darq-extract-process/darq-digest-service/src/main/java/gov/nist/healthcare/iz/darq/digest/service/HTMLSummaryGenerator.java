package gov.nist.healthcare.iz.darq.digest.service;

import java.io.IOException;
import java.util.Map;
import gov.nist.healthcare.iz.darq.digest.domain.Summary;

public interface HTMLSummaryGenerator {

	void generateSummary(Summary summary, Map<String, String> providers, String path) throws IOException;
	
}
