package gov.nist.healthcare.iz.darq.digest.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.crypto.service.impl.JKSCryptoKey;
import gov.nist.healthcare.iz.darq.digest.app.CLIApp;
import gov.nist.healthcare.iz.darq.test.data.DataExtractMock;
import gov.nist.healthcare.iz.darq.test.helper.Constants;
import gov.nist.healthcare.iz.darq.test.helper.Record;
import org.apache.commons.io.FileUtils;
import org.junit.rules.TemporaryFolder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CLITestRunnerUtils {

	DataExtractMock mock;
	ObjectMapper objectMapper = new ObjectMapper();
	TemporaryFolder folder;
	protected Path configurationFilePath;
	protected Path patientsFilePath;
	protected Path vaccinationsFilePath;
	protected CryptoKey cryptoKey;

	public CLITestRunnerUtils(DataExtractMock mock, TemporaryFolder folder) {
		this.mock = mock;
		this.folder = folder;
	}

	public void createFiles() throws IOException {
		System.out.println("=== Creating Configuration File");
		configurationFilePath = folder.newFile("configuration.json").toPath();
		objectMapper.writeValue(new FileWriter(configurationFilePath.toFile()), mock.getConfigurationPayload());
		System.out.println(String.join("\n", Files.readAllLines(configurationFilePath)));
		System.out.println();
		System.out.println("=== Creating Extract File");
		patientsFilePath = folder.newFile("patients.tsv").toPath();
		vaccinationsFilePath = folder.newFile("vaccinations.tsv").toPath();
		FileWriter patients = new FileWriter(patientsFilePath.toFile());
		FileWriter vaccinations = new FileWriter(vaccinationsFilePath.toFile());
		for(Record record: mock.getDataExtract()) {
			patients.write(record.getPatient());
			patients.write("\n");
			for(String vaccination: record.getVaccinations()) {
				vaccinations.write(vaccination);
				vaccinations.write("\n");
			}
		}
		patients.close();
		vaccinations.close();
		System.out.println("\nPatients File");
		System.out.println(FileUtils.readFileToString(patientsFilePath.toFile(), "UTF-8"));
		System.out.println("\nVaccinations File");
		System.out.println(FileUtils.readFileToString(vaccinationsFilePath.toFile(), "UTF-8"));
	}

	public void runCLI() throws Exception {
		System.out.println();
		cryptoKey = new JKSCryptoKey(CLITestRunnerUtils.class.getResourceAsStream(Constants.TEST_KEY), Constants.TEST_KEY_ALIAS, Constants.TEST_KEY_PASSWORD, Constants.TEST_KEY_PASSWORD);
		InputStream PUBLIC_KEY = CLITestRunnerUtils.class.getResourceAsStream(Constants.TEST_KEY_PUBLIC);
		Path pkPath = Paths.get(folder.getRoot().getAbsolutePath(), "public.pem");
		FileUtils.copyInputStreamToFile(PUBLIC_KEY, pkPath.toFile());
		String[] args = new String[5];
		args[0] = "-p=" + patientsFilePath.toAbsolutePath();
		args[1] = "-v=" + vaccinationsFilePath.toAbsolutePath();
		args[2] = "-c=" + configurationFilePath.toAbsolutePath();
		args[3] = "-pub=" + pkPath.toAbsolutePath();
		args[4] = "-out=" + folder.getRoot().getAbsolutePath();
		try {
			CLIApp.run(args);
		} finally {
			CLIApp.cleanUp();
		}
	}

	public Path getPatientsFilePath() {
		return patientsFilePath;
	}

	public Path getVaccinationsFilePath() {
		return vaccinationsFilePath;
	}

	public Path getConfigurationFilePath() {
		return configurationFilePath;
	}

	public CryptoKey getCryptoKey() {
		return cryptoKey;
	}
}
