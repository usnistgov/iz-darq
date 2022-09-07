package gov.nist.healthcare.iz.darq.adf.service;


import java.io.IOException;
import java.io.InputStream;

import gov.nist.healthcare.iz.darq.digest.domain.ADFMetaData;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;

public interface ADFStore<T extends ADFMetaData> {

	String store(T metadata);
	T get(String id);
	ADFile getFile(String id) throws Exception;
	InputStream getFileInputStream(String id) throws Exception;
	boolean delete(String id) throws IOException;
	
}
