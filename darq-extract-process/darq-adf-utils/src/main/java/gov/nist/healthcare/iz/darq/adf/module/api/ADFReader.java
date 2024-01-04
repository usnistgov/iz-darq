package gov.nist.healthcare.iz.darq.adf.module.api;

import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import gov.nist.healthcare.iz.darq.adf.model.Metadata;
import gov.nist.healthcare.iz.darq.adf.utils.crypto.ADFCryptoUtil;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Summary;

import java.io.IOException;
import java.nio.file.Path;

public interface ADFReader extends AutoCloseable {
	ADFCryptoUtil crypto = new ADFCryptoUtil();

	void open(CryptoKey key) throws Exception;
	void read(CryptoKey key) throws Exception;
	void close() throws Exception;
	ConfigurationPayload getConfigurationPayload();
	Metadata getMetadata();
	Summary getSummary();
	boolean isOpen();
	boolean isReady();
	ADFVersion getVersion();
	long getFileSize() throws IOException;
	boolean checkADFKey(CryptoKey key) throws Exception;
	Path getADFLocation();
}
