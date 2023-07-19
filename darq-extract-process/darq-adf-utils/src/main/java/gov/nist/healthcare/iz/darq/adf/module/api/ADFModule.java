package gov.nist.healthcare.iz.darq.adf.module.api;

import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.exception.UnsupportedADFVersion;
import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ADFModule {
	ADFWriter getWriter(CryptoKey key) throws UnsupportedADFVersion;
	ADFReader getReader(String location) throws UnsupportedADFVersion;
	ADFVersion getVersion();
	boolean isInstanceOfVersion(BufferedInputStream content) throws IOException, UnsupportedADFVersion;
	OutputStream merge(InputStream a, InputStream b) throws UnsupportedADFVersion;
}
