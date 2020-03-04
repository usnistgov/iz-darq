package gov.nist.healthcare.iz.darq.adf.utils.crypto.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.undercouch.bson4jackson.BsonFactory;
import gov.nist.healthcare.iz.darq.adf.utils.crypto.CryptoUtils;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;
import gov.nist.healthcare.iz.darq.digest.domain.EncryptedADF;

@Service
public class CryptoUtilsImpl implements CryptoUtils {
	
	@Value("#{environment.DARQ_KEY}")
	private String PRIVATE_KEY;
	private ObjectMapper mapper = new ObjectMapper(new BsonFactory());

	@Override
	public byte[] encrypt(ADFile file) throws Exception {
		
		//----------------- AES ENCRYPT ------------------
		SecretKeySpec aesKey = password();
		final Cipher aes = Cipher.getInstance("AES");
	    aes.init(Cipher.ENCRYPT_MODE, aesKey);
	    byte[] fileContent = serialize(ADFile.class, file);
	    byte[] encryptedContent = aes.doFinal(fileContent);
	    
	    //------------------ ENCRYPT KEY -----------------
	    final Cipher cipher = Cipher.getInstance("RSA");
	    cipher.init(Cipher.ENCRYPT_MODE, pub());
	    byte[] encryptedKey = cipher.doFinal(aesKey.getEncoded());
	    
	    return serialize(EncryptedADF.class, new EncryptedADF(encryptedKey, encryptedContent));
	}

	@Override
	public ADFile decrypt(byte[] bytes) throws Exception {
		EncryptedADF encryptedADF = deserialize(EncryptedADF.class, bytes);
		
		//----------------- DECRYPT AES ------------------
		final Cipher cipher = Cipher.getInstance("RSA");
	    cipher.init(Cipher.DECRYPT_MODE, priv());
	    byte[] decryptedKey = cipher.doFinal(encryptedADF.getKey());
	    
		//----------------- DECRYPT FILE -----------------
	    SecretKeySpec secretKeySpec = new SecretKeySpec(decryptedKey, "AES");
	    Cipher cipherAes = Cipher.getInstance("AES");
	    cipherAes.init(Cipher.DECRYPT_MODE, secretKeySpec);
	    byte[] decryptedBytes = cipherAes.doFinal(encryptedADF.content);
	    
		return deserialize(ADFile.class, decryptedBytes);
	}
	
	private PublicKey pub() throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] cert_bytes = IOUtils.toByteArray(CryptoUtilsImpl.class.getResourceAsStream("/certificate.pub"));
	    X509EncodedKeySpec ks = new X509EncodedKeySpec(cert_bytes);
	    KeyFactory kf = KeyFactory.getInstance("RSA");
	    return kf.generatePublic(ks);
	}
	
	private PrivateKey priv() throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] bytes = IOUtils.toByteArray(new FileInputStream(PRIVATE_KEY+"/certificate.key"));
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
	
	private <T> byte[] serialize(Class<T> type, T adf) throws JsonGenerationException, JsonMappingException, IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		mapper.writeValue(baos, adf);
		return baos.toByteArray();
	}
	
	private <T> T deserialize(Class<T> type, byte[] bytes) throws JsonGenerationException, JsonMappingException, IOException {
		return mapper.readValue(bytes, type);
	}

}