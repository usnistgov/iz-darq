package gov.nist.healthcare.iz.darq.digest.service;

import java.nio.file.Path;
import gov.nist.healthcare.iz.darq.digest.domain.ADChunk;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;

public interface ExportADChunk {
	
	void export(ConfigurationPayload payload, Path folder, ADChunk chunk, String version, String build, String mqeVersion, long elapsed, boolean excludeAdfFromSummary) throws Exception;

}
