package gov.nist.healthcare.iz.darq.adf.service;

import java.io.InputStream;

import gov.nist.healthcare.iz.darq.adf.service.exception.InvalidFileFormat;

public interface ADFStoreUploadHandler {
	
	void handle(String name, InputStream stream, String owner, long size) throws InvalidFileFormat;
	
}
