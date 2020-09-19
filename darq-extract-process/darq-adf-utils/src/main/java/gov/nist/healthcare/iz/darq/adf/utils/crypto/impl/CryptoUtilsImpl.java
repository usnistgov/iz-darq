package gov.nist.healthcare.iz.darq.adf.utils.crypto.impl;

import java.io.*;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.undercouch.bson4jackson.BsonFactory;
import gov.nist.healthcare.iz.darq.adf.utils.crypto.CryptoUtils;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;
import gov.nist.healthcare.iz.darq.digest.domain.EncryptedADF;

@Service
public class CryptoUtilsImpl implements CryptoUtils {
	
	@Value("#{environment.DARQ_KEY}")
	private String CERTS;
	private final ObjectMapper mapper = new ObjectMapper(new BsonFactory()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	@Override
	public void encryptContentToFile(ADFile file, OutputStream outputStream) throws Exception {
		
		//----------------- AES ENCRYPT ------------------
		SecretKeySpec aesKey = password();
		final Cipher aes = Cipher.getInstance("AES");
	    aes.init(Cipher.ENCRYPT_MODE, aesKey);
	    byte[] fileContent = serialize(ADFile.class, file);
	    byte[] encryptedContent = aes.doFinal(fileContent);
	    
	    //------------------ ENCRYPT KEY -----------------
	    final Cipher cipher = Cipher.getInstance("RSA");
	    cipher.init(Cipher.ENCRYPT_MODE, publicKey());
	    byte[] encryptedKey = cipher.doFinal(aesKey.getEncoded());

		mapper.writeValue(outputStream, new EncryptedADF(encryptedKey, encryptedContent));
	}

	@Override
	public ADFile decryptFile(InputStream inputStream) throws Exception {
		EncryptedADF encryptedADF = mapper.readValue(inputStream, EncryptedADF.class);
		
		//----------------- DECRYPT AES ------------------
		final Cipher cipher = Cipher.getInstance("RSA");
	    cipher.init(Cipher.DECRYPT_MODE, privateKey());
	    byte[] decryptedKey = cipher.doFinal(encryptedADF.getKey());
	    
		//----------------- DECRYPT FILE -----------------
	    SecretKeySpec secretKeySpec = new SecretKeySpec(decryptedKey, "AES");
	    Cipher cipherAes = Cipher.getInstance("AES");
	    cipherAes.init(Cipher.DECRYPT_MODE, secretKeySpec);
	    byte[] decryptedBytes = cipherAes.doFinal(encryptedADF.content);
	    
		return deserialize(ADFile.class, decryptedBytes);
	}
	
	public PublicKey publicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] cert_bytes = IOUtils.toByteArray(CryptoUtilsImpl.class.getResourceAsStream("/certificate.pub"));
	    X509EncodedKeySpec ks = new X509EncodedKeySpec(cert_bytes);
	    KeyFactory kf = KeyFactory.getInstance("RSA");
	    return kf.generatePublic(ks);
	}

	public PrivateKey privateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] bytes = IOUtils.toByteArray(new FileInputStream(CERTS+"/certificate.key"));
		PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(ks);
	}
	
	private SecretKeySpec password() throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
	    keyGen.init(128);
	    SecretKey skey = keyGen.generateKey();
		return new SecretKeySpec(skey.getEncoded(), "AES");
	}
	
	private <T> byte[] serialize(Class<T> type, T adf) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		mapper.writeValue(baos, adf);
		return baos.toByteArray();
	}
	
	private <T> T deserialize(Class<T> type, byte[] bytes) throws IOException {
		return mapper.readValue(bytes, type);
	}

}
