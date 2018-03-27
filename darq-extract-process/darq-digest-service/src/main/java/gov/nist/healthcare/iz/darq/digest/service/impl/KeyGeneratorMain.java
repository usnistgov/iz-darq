package gov.nist.healthcare.iz.darq.digest.service.impl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class KeyGeneratorMain {

	public static String fileBase = "/Users/hnt5/darq-keys/certificate";
	
	public static void main(String[] args) throws NoSuchAlgorithmException, FileNotFoundException, IOException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(2048);
		KeyPair kp = kpg.generateKeyPair();
		try (FileOutputStream out = new FileOutputStream(fileBase + ".key")) {
		    out.write(kp.getPrivate().getEncoded());
		}

		try (FileOutputStream out = new FileOutputStream(fileBase + ".pub")) {
		    out.write(kp.getPublic().getEncoded());
		}
	}
}
