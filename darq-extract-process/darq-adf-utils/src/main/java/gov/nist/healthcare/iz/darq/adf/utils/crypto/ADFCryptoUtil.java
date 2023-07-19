package gov.nist.healthcare.iz.darq.adf.utils.crypto;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.security.Key;

public class ADFCryptoUtil {

	public SecretKeySpec generateAESKey() throws Exception {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(128);
		SecretKey key = keyGen.generateKey();
		return new SecretKeySpec(key.getEncoded(), "AES");
	}

	public CipherInputStream decodeRSA(InputStream content, Key key) throws Exception {
		final Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, key);
		return new CipherInputStream(content, cipher);
	}

	public CipherInputStream decodeAES(InputStream content, byte[] key) throws Exception {
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
		final Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
		return new CipherInputStream(content, cipher);
	}

	public CipherInputStream encodeRSA(InputStream content, Key key) throws Exception {
		final Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return new CipherInputStream(content, cipher);
	}

	public CipherInputStream encodeAES(InputStream content, byte[] key) throws Exception {
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
		final Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		return new CipherInputStream(content, cipher);
	}
}
