package gov.nist.healthcare.iz.darq.adf.service;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.digest.domain.ADFMetaData;

public interface ADFStore<T extends ADFMetaData> {

	String store(T metadata);
	T get(String id);
	ADFReader getFile(String id) throws Exception;
	InputStream getFileInputStream(String id) throws Exception;
	boolean delete(String id) throws IOException;
	Path getFilePath(String id);

}
