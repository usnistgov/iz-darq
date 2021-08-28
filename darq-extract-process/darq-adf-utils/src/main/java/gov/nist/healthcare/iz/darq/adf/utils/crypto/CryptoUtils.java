package gov.nist.healthcare.iz.darq.adf.utils.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;

import java.io.*;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public interface CryptoUtils {

	boolean checkAdfStore(InputStream fileInputStream) throws Exception;
	void encryptContentToFile(ADFile file, OutputStream writer) throws Exception;
	ADFile decryptFile(InputStream reader) throws Exception;
	ObjectMapper getObjectMapper();

}
