package gov.nist.healthcare.iz.darq.digest.detections;

import gov.nist.healthcare.iz.darq.adf.module.sqlite.SqliteADFReader;
import gov.nist.healthcare.iz.darq.adf.module.sqlite.model.Dictionaries;
import gov.nist.healthcare.iz.darq.detections.codes.VaccinationDuplicateDetection;
import gov.nist.healthcare.iz.darq.digest.common.CLITestRunnerUtils;
import gov.nist.healthcare.iz.darq.digest.common.SQLiteADFTestUtils;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.mock.DuplicateVaccinationMock;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.util.DigestUtils;

import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DuplicateVaccinationDetectionTestCase {
	static TemporaryFolder folder = new TemporaryFolder();
	static SqliteADFReader reader;
	static DuplicateVaccinationMock mock;
	static CLITestRunnerUtils utils;
	static SQLiteADFTestUtils sqliteAdfHelper;

	@BeforeClass
	public static void setup() throws Exception {
		folder.create();
		mock = DuplicateVaccinationMock.get();
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
	public void checkComplexDetections() throws Exception {
		String DETECTION_CODE = VaccinationDuplicateDetection.VD0001.name();
		Dictionaries d = reader.getDictionaries();
		PreparedStatement search = reader.getConnection().prepareStatement("SELECT (count(*) = 1) as found FROM V_DETECTIONS WHERE PROVIDER_ID = ? AND AGE_GROUP = ? AND DETECTION_CODE = ? AND P = ? AND N = ?");
		PreparedStatement count = reader.getConnection().prepareStatement("SELECT count(*) as nb FROM V_DETECTIONS");
		List<List<Integer>> rows = Arrays.asList(
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("ANY".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.DETECTION, DETECTION_CODE), 6, 3)
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