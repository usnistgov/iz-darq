package gov.nist.healthcare.iz.darq.installer.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class InstallHelper {

	public static void generateRSAKeys(String folder) throws FileNotFoundException, IOException, NoSuchAlgorithmException{
		if(!new File(folder).exists()){
			new File(folder).mkdirs();
		}
		
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(2048);
		KeyPair kp = kpg.generateKeyPair();
		try (FileOutputStream out = new FileOutputStream(folder + "/certificate.key")) {
		    out.write(kp.getPrivate().getEncoded());
		}

		try (FileOutputStream out = new FileOutputStream(folder + "/certificate.pub")) {
		    out.write(kp.getPublic().getEncoded());
		}
	}
	
}
