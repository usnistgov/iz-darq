package gov.nist.healthcare.iz.darq.adf.module.api;

import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.exception.UnsupportedADFVersion;
import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import java.util.List;

public interface ADFModule {
	ADFWriter getWriter(CryptoKey key) throws UnsupportedADFVersion;
	ADFReader getReader(String location) throws UnsupportedADFVersion;
	ADFVersion getVersion();
	boolean isInstanceOfVersion(String file) throws Exception;
	void merge(List<ADFReader> files, CryptoKey key, String targetLocation) throws Exception;
}
