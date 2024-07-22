package gov.nist.healthcare.iz.darq.adf.service;

import java.io.InputStream;
import java.util.List;

import gov.nist.healthcare.iz.darq.adf.service.exception.InvalidFileFormat;

public interface ADFStoreUploadHandler {
	
	void handle(String name, List<String> tags, String facility, InputStream stream, String ownerId, long size) throws InvalidFileFormat;
	
}
