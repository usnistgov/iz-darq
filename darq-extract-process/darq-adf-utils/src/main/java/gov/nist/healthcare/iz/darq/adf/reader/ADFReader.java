package gov.nist.healthcare.iz.darq.adf.reader;

import gov.nist.healthcare.iz.darq.adf.model.Metadata;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Summary;

import java.nio.file.Path;

public interface ADFReader {

	void load() throws Exception;
	void close() throws Exception;
	ConfigurationPayload getConfigurationPayload();
	Metadata getMetadata();
	Summary getSummary();
	boolean checkADFKey();
	Path getADFLocation();
	String getADFVersion();

}
