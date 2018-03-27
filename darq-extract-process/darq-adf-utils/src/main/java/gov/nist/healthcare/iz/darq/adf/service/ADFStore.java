package gov.nist.healthcare.iz.darq.adf.service;


import java.io.IOException;

import gov.nist.healthcare.iz.darq.digest.domain.ADFMetaData;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;

public interface ADFStore {

	String store(ADFMetaData metadata);
	ADFMetaData get(String id, String owner);
	ADFile getFile(String id, String owner) throws Exception;
	boolean delete(String id, String owner) throws IOException;
	
}
