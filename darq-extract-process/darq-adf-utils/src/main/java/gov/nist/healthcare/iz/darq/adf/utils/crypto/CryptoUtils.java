package gov.nist.healthcare.iz.darq.adf.utils.crypto;

import gov.nist.healthcare.iz.darq.digest.domain.ADFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public interface CryptoUtils {
	
	byte[] encrypt(ADFile file) throws Exception;
	ADFile decrypt(byte[] bytes) throws Exception;
	PublicKey publicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException;
	PrivateKey privateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException;

}
