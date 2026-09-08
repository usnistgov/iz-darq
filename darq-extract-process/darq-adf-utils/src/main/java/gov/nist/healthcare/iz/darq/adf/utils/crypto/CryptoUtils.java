package gov.nist.healthcare.iz.darq.adf.utils.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nist.healthcare.iz.darq.adf.module.json.model.ADFile;

import java.io.InputStream;
import java.io.OutputStream;

public interface CryptoUtils {

	boolean checkAdfStore(InputStream fileInputStream) throws Exception;
	void encryptContentToFile(ADFile file, OutputStream writer) throws Exception;
	void encryptContentToFileWithoutTemporaryFile(ADFile file, OutputStream writer) throws Exception;
	ADFile decryptFile(InputStream reader) throws Exception;
	ObjectMapper getObjectMapper();

}
