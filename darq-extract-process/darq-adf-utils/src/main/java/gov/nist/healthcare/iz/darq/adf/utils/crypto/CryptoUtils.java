package gov.nist.healthcare.iz.darq.adf.utils.crypto;

import gov.nist.healthcare.iz.darq.digest.domain.ADFile;

public interface CryptoUtils {
	
	byte[] encrypt(ADFile file) throws Exception;
	ADFile decrypt(byte[] bytes) throws Exception;

}
