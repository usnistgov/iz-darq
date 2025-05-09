package gov.nist.healthcare.iz.darq.digest.adf;

import gov.nist.healthcare.iz.darq.adf.module.sqlite.SqliteADFReader;
import gov.nist.healthcare.iz.darq.adf.module.sqlite.model.Dictionaries;
import gov.nist.healthcare.iz.darq.digest.common.CLITestRunnerUtils;
import gov.nist.healthcare.iz.darq.digest.common.SQLiteADFTestUtils;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.test.data.mocks.DuplicatePatientExampleMock;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.sql.PreparedStatement;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DuplicatePatientRecordTestCase {
	static TemporaryFolder folder = new TemporaryFolder();
	static SqliteADFReader reader;
	static DuplicatePatientExampleMock mock;
	static CLITestRunnerUtils utils;
	static SQLiteADFTestUtils sqliteAdfHelper;

	@BeforeClass
	public static void setup() throws Exception {
		folder.create();
		mock = DuplicatePatientExampleMock.get();
		utils = new CLITestRunnerUtils(mock, folder);
		sqliteAdfHelper = new SQLiteADFTestUtils();
		utils.createFiles();
		utils.runCLI();
		reader = sqliteAdfHelper.readADF(utils.getCryptoKey(), folder);
		assertTrue(reader.isReady() && reader.isOpen());
	}

	@Test
	public void configurationPayloadExpectation() {
		assertEquals(mock.getConfigurationPayload(), reader.getConfigurationPayload());
	}


	@Test
	public void checkPatientDetections() throws Exception {
		Dictionaries d = reader.getDictionaries();
		PreparedStatement search = reader.getConnection().prepareStatement("SELECT (count(*) = 1) as found FROM P_DETECTIONS WHERE AGE_GROUP = ? AND DETECTION_CODE = ? AND P = ? AND N = ?");
		PreparedStatement count = reader.getConnection().prepareStatement("SELECT count(*) as nb FROM P_DETECTIONS");
		List<List<Integer>> rows = Arrays.asList(
				Arrays.asList(d.findId(Field.AGE_GROUP, "0g"), d.findId(Field.DETECTION, "PM001"), 2, 1),
				Arrays.asList(d.findId(Field.AGE_GROUP, "1g"), d.findId(Field.DETECTION, "PM001"), 1, 1)
		);
		sqliteAdfHelper.checkTableContent(search, count, rows);
	}

	@Test
	public void checkDuplicateSignatures() throws Exception {
		String SIGNATURE = "cK9E6EH243DMd14:hKAAQcA:hKAAQcA:hKAAQcA:hKAAQcA";
		Dictionaries d = reader.getDictionaries();
		PreparedStatement search = reader.getConnection().prepareStatement("SELECT (count(*) = 1) as found FROM P_MATCH_SIGNATURE WHERE MATCH_SIGNATURE = ? AND N = ?");
		PreparedStatement count = reader.getConnection().prepareStatement("SELECT count(*) as nb FROM P_MATCH_SIGNATURE");
		List<List<Integer>> rows = Arrays.asList(
				Arrays.asList(d.findId(Field.MATCH_SIGNATURE, SIGNATURE), 2)
		);
		sqliteAdfHelper.checkTableContent(search, count, rows);
	}

	@AfterClass
	public static void close() throws Exception {
		if(reader != null) {
			reader.close();
		}
	}
}
