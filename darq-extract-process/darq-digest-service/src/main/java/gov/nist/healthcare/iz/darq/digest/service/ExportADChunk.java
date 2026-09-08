package gov.nist.healthcare.iz.darq.digest.service;

import gov.nist.healthcare.iz.darq.adf.module.api.ADFWriter;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;

import java.nio.file.Path;

public interface ExportADChunk {
	
	void export(ConfigurationPayload payload, Path folder, ADFWriter writer, String version, String build, String mqeVersion, long elapsed, boolean excludeAdfFromSummary) throws Exception;

}
