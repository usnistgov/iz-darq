package gov.nist.healthcare.iz.darq.digest.detections;

import gov.nist.healthcare.iz.darq.adf.module.sqlite.SqliteADFReader;
import gov.nist.healthcare.iz.darq.adf.module.sqlite.model.Dictionaries;
import gov.nist.healthcare.iz.darq.digest.common.CLITestRunnerUtils;
import gov.nist.healthcare.iz.darq.digest.common.SQLiteADFTestUtils;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.mock.complex.ComplexDetectionRecordLevelMock;
import org.immregistries.mqe.validator.detection.Detection;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ComplexDetectionRecordLevelTestCase {
	static TemporaryFolder folder = new TemporaryFolder();
	static SqliteADFReader reader;
	static ComplexDetectionRecordLevelMock mock;
	static CLITestRunnerUtils utils;
	static SQLiteADFTestUtils sqliteAdfHelper;

	@BeforeClass
	public static void setup() throws Exception {
		folder.create();
		mock = ComplexDetectionRecordLevelMock.get();
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
		Dictionaries d = reader.getDictionaries();
		String COMPLEX_DETECTION_CODE = ComplexDetectionRecordLevelMock.PatientIsPossibleDuplicateAndAddressStreetIsMissing;
		PreparedStatement search = reader.getConnection().prepareStatement("SELECT (count(*) = 1) as found FROM P_DETECTIONS WHERE AGE_GROUP = ? AND DETECTION_CODE = ? AND P = ? AND N = ?");
		PreparedStatement count = reader.getConnection().prepareStatement("SELECT count(*) as nb FROM P_DETECTIONS");
		List<List<Integer>> rows = Arrays.asList(
				Arrays.asList(d.findId(Field.AGE_GROUP, "0g"), d.findId(Field.DETECTION, "PM001"), 2, 1),
				Arrays.asList(d.findId(Field.AGE_GROUP, "0g"), d.findId(Field.DETECTION, Detection.PatientAddressStreetIsMissing.getMqeMqeCode()), 1, 2),
				Arrays.asList(d.findId(Field.AGE_GROUP, "0g"), d.findId(Field.DETECTION, COMPLEX_DETECTION_CODE), 2, 1),// Complex Detection
				Arrays.asList(d.findId(Field.AGE_GROUP, "1g"), d.findId(Field.DETECTION, "PM001"), 1, 1),
				Arrays.asList(d.findId(Field.AGE_GROUP, "1g"), d.findId(Field.DETECTION, Detection.PatientAddressStreetIsMissing.getMqeMqeCode()), 2, 0),
				Arrays.asList(d.findId(Field.AGE_GROUP, "1g"), d.findId(Field.DETECTION, COMPLEX_DETECTION_CODE), 2, 0) // Complex Detection
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
