package gov.nist.healthcare.iz.darq.digest.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.undercouch.bson4jackson.BsonFactory;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;
import gov.nist.healthcare.iz.darq.digest.domain.EncryptedADF;

public class DecryptMain {
	
	public static void main(String[] args) throws Exception {
		//deserialize data
		byte[] bytes = FileUtils.readFileToByteArray(Paths.get("/Users/hnt5/darq-keys/certificate.key").toFile());
		PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PrivateKey pvt = kf.generatePrivate(ks);
		
		ObjectMapper mapper = new ObjectMapper(new BsonFactory());
	    ByteArrayInputStream bais = new ByteArrayInputStream(FileUtils.readFileToByteArray(new File("/Users/hnt5/SharedProjects/iz-darq/darq-data-digest/darq-analysis/ADF.data")));
	    EncryptedADF encryptedADF = mapper.readValue(bais, EncryptedADF.class);
	    
	    final Cipher cipher = Cipher.getInstance("RSA");
	    cipher.init(Cipher.DECRYPT_MODE, pvt);
	    byte[] decryptedKey = cipher.doFinal(encryptedADF.getKey());
	    
	    SecretKeySpec secretKeySpec = new SecretKeySpec(decryptedKey, "AES");
	    
	    Cipher cipherAes = Cipher.getInstance("AES");
	    cipherAes.init(Cipher.DECRYPT_MODE, secretKeySpec);
	    byte[] decryptedBytes = cipherAes.doFinal(encryptedADF.content);
	    
	    ADFile file = mapper.readValue(decryptedBytes, ADFile.class);
	    ObjectMapper writeMapper = new ObjectMapper();
	    writeMapper.writeValue(Paths.get("/Users/hnt5/SharedProjects/iz-darq/darq-data-digest/darq-analysis/DE_ADF.data").toFile(), file);
	    
	}

}
