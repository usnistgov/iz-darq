package gov.nist.healthcare.iz.darq.installer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.jar.JarFile;

import gov.nist.healthcare.iz.darq.installer.utils.InstallHelper;
import gov.nist.war.edit.domain.Properties;
import gov.nist.war.edit.service.impl.ArtifactEditService;

public class Installer {
	
	public static Scanner scanner = new Scanner(System.in);
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchAlgorithmException, IOException {
		String OUT, DB_HOST, DB_PORT, DB_NAME, KEY_FOLDER, GENERATE_KEY, ADF_FOLDER, ADMIN_PASSWORD;
		ArtifactEditService service = new ArtifactEditService();
		
		System.out.println("Welcome to DARQ Webapp Installer");
		
		
		do {
			System.out.print("Output folder ? ");
			OUT = getValue("");
		}
		while(OUT == null || OUT.isEmpty());
		
		System.out.print("Database host (default : localhost) ? ");
		DB_HOST = getValue("localhost");
		
		System.out.print("Database port (default : 27017) ? ");
		DB_PORT = getValue("27017");
		
		System.out.print("Database name (default : darq-db) ? ");
		DB_NAME = getValue("darq-db");
		
		
		do {
			System.out.print("RSA keys folder (certificate.pub, certificate.key) ? ");
			KEY_FOLDER = getValue("");
		}
		while(KEY_FOLDER == null || KEY_FOLDER.isEmpty());
		
		do {
			System.out.print("Generate RSA keys (yes/no) ? ");
			GENERATE_KEY = getValue("");
		}
		while(GENERATE_KEY == null || GENERATE_KEY.isEmpty());
		
		do {
			System.out.print("ADF files storage folder ? ");
			ADF_FOLDER = getValue("");
		}
		while(ADF_FOLDER == null || ADF_FOLDER.isEmpty());
		
		do {
			System.out.print("ADMIN password ? ");
			ADMIN_PASSWORD = getValue("");
		}
		while(ADMIN_PASSWORD == null || ADMIN_PASSWORD.isEmpty());
		
		
		
		
		Properties confProps = new Properties("configuration.properties");
		confProps.put("darq.keys", KEY_FOLDER);
		confProps.put("darq.store", ADF_FOLDER);
		
		Properties appProps = new Properties("application.properties");
		appProps.put("darq.db.host", DB_HOST);
		appProps.put("darq.db.port", DB_PORT);
		appProps.put("darq.db.name", DB_NAME);
		appProps.put("darq.admin.default", ADMIN_PASSWORD);
		
		if(GENERATE_KEY.toLowerCase().equals("yes")){
			System.out.println("Generating RSA keys");
			InstallHelper.generateRSAKeys(KEY_FOLDER);
		}
		else {
			if(!new File(KEY_FOLDER+"/certificate.pub").exists() || !new File(KEY_FOLDER+"/certificate.key").exists()){
				System.out.println("Please make sure to put your RSA key in the aforementioned folder : "+KEY_FOLDER);
				System.exit(0);
			}
		}
		
		Map<String, File> files = new HashMap<>();
		files.put("certificate.pub", new File(KEY_FOLDER+"/certificate.pub"));
		
		// 1 - Create Output Folder
		if(!new File(OUT).exists()) new File(OUT).mkdirs();
		
		System.out.println("Preparing CLI App");
		// 2 - Create CLI Jar
		service.editProperties(new JarFile(Installer.class.getResource("/darq-cli.jar").getFile()), OUT+"/darq-cli.jar", null, files);
		
		// 4 - Create file mapping for the new Cli Jar
		Map<String, File> jar = new HashMap<>();
		jar.put("darq-cli.jar", new File(OUT+"/darq-cli.jar"));
		
		System.out.println("Preparing WebApp");
		// 4 - Create JAR WebApp
		service.editProperties(new JarFile(Installer.class.getResource("/darq-webapp.war").getFile()), OUT+"/darq-webapp.jar", Arrays.asList(confProps, appProps), jar);
		
	}
	
	
	
	static String getValue(String def) {
		String value = scanner.nextLine();
		if(value == null || value.isEmpty()){
			return def;
		}
		return value;
	}
}
