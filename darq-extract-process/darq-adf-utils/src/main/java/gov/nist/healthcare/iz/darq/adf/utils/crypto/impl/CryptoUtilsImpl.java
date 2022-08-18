package gov.nist.healthcare.iz.darq.adf.utils.crypto.impl;

import java.io.*;
import java.security.*;
import java.util.Arrays;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.databind.*;
import de.undercouch.bson4jackson.BsonGenerator;
import gov.nist.healthcare.crypto.service.CryptoKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.undercouch.bson4jackson.BsonFactory;
import gov.nist.healthcare.iz.darq.adf.utils.crypto.CryptoUtils;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;
import gov.nist.healthcare.iz.darq.digest.domain.EncryptedADF;

@Service
public class CryptoUtilsImpl implements CryptoUtils {
	public final static String PUB_KEY_RESOURCE_NAME = "public.der";

	@Autowired
	@Qualifier("ADF_KEYS")
	private CryptoKey keys;
	private final ObjectMapper mapper = new ObjectMapper(new BsonFactory().enable(BsonGenerator.Feature.ENABLE_STREAMING)).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	@Override
	public boolean checkAdfStore(InputStream fileInputStream) throws Exception {
		EncryptedADF encryptedADF = mapper.readValue(fileInputStream, EncryptedADF.class);
		byte[] currentKeyHash = this.keys.getPublicKeyHash();
		return Arrays.equals(currentKeyHash, encryptedADF.keyHash);
	}

	@Override
	public void encryptContentToFile(ADFile file, OutputStream outputStream) throws Exception {

		//----------------- Create TEMP File -------------
		File tempFile = File.createTempFile("enc", ".adf");
		tempFile.deleteOnExit();

		//----------------- AES ENCRYPT ------------------
		SecretKeySpec aesKey = password();
		final Cipher aes = Cipher.getInstance("AES");
	    aes.init(Cipher.ENCRYPT_MODE, aesKey);
		CipherOutputStream aesOut = new CipherOutputStream(new FileOutputStream(tempFile), aes);
		mapper.writeValue(aesOut, file);
	    
	    //------------------ ENCRYPT KEY -----------------
		PublicKey publicKey = keys.getPublicKey();
	    final Cipher cipher = Cipher.getInstance("RSA");
	    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
	    byte[] encryptedKey = cipher.doFinal(aesKey.getEncoded());

		mapper.writeValue(outputStream, new EncryptedADF(encryptedKey, new FileInputStream(tempFile), this.keys.getPublicKeyHash()));
		tempFile.delete();
	}

	@Override
	public void encryptContentToFileWithoutTemporaryFile(ADFile file, OutputStream outputStream) throws Exception {
		//----------------- AES ENCRYPT ------------------
		SecretKeySpec aesKey = password();
		final Cipher aes = Cipher.getInstance("AES");
		aes.init(Cipher.ENCRYPT_MODE, aesKey);
		byte[] fileContent = serialize(ADFile.class, file);
		byte[] encryptedContent = aes.doFinal(fileContent);

		//------------------ ENCRYPT KEY -----------------
		PublicKey publicKey = keys.getPublicKey();
		final Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encryptedKey = cipher.doFinal(aesKey.getEncoded());
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(encryptedContent);

		mapper.writeValue(outputStream, new EncryptedADF(encryptedKey, byteArrayInputStream, this.keys.getPublicKeyHash()));
	}

	@Override
	public ADFile decryptFile(InputStream inputStream) throws Exception {
		EncryptedADF encryptedADF = mapper.readValue(inputStream, EncryptedADF.class);
		//----------------- DECRYPT AES ------------------
		final Cipher cipher = Cipher.getInstance("RSA");
	    cipher.init(Cipher.DECRYPT_MODE, keys.getPrivateKey());
	    byte[] decryptedKey = cipher.doFinal(encryptedADF.getKey());
		//----------------- DECRYPT FILE -----------------
	    SecretKeySpec secretKeySpec = new SecretKeySpec(decryptedKey, "AES");
	    Cipher cipherAes = Cipher.getInstance("AES");
	    cipherAes.init(Cipher.DECRYPT_MODE, secretKeySpec);
		CipherInputStream aesIn = new CipherInputStream(encryptedADF.content, cipherAes);
		return deserialize(ADFile.class, aesIn);
	}

	@Override
	public ObjectMapper getObjectMapper() {
		return this.mapper;
	}

	private SecretKeySpec password() throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
	    keyGen.init(128);
	    SecretKey skey = keyGen.generateKey();
		return new SecretKeySpec(skey.getEncoded(), "AES");
	}

	private <T> T deserialize(Class<T> type, InputStream is) throws IOException {
		return mapper.readValue(is, type);
	}

	private <T> byte[] serialize(Class<T> type, T adf) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		mapper.writeValue(baos, adf);
		return baos.toByteArray();
	}

}
