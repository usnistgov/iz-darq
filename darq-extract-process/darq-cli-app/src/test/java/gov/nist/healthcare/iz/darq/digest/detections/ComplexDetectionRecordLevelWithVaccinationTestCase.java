package gov.nist.healthcare.iz.darq.digest.detections;

import gov.nist.healthcare.iz.darq.adf.module.sqlite.SqliteADFReader;
import gov.nist.healthcare.iz.darq.adf.module.sqlite.model.Dictionaries;
import gov.nist.healthcare.iz.darq.digest.common.CLITestRunnerUtils;
import gov.nist.healthcare.iz.darq.digest.common.SQLiteADFTestUtils;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.mock.complex.ComplexDetectionMultiOpMock;
import gov.nist.healthcare.iz.darq.digest.mock.complex.ComplexDetectionRecordLevelWithVaccinationMock;
import org.immregistries.mqe.validator.detection.Detection;
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

/*
	Test about Record Level Detection using Vaccination Detection Codes
		-> Patient address street is present IMPLY Vaccine CVX is unrecognized
		-> 3 records
			- Street Present, 2 vaccinations CVX recognized -> Not triggered
			- Street Missing, 1 vaccination CVX unrecognized -> Not triggered
			- Street Present, 1 vx CVX recognized, 1 vx CVX unrecognized -> Triggered
 */
public class ComplexDetectionRecordLevelWithVaccinationTestCase {
	static TemporaryFolder folder = new TemporaryFolder();
	static SqliteADFReader reader;
	static ComplexDetectionRecordLevelWithVaccinationMock mock;
	static CLITestRunnerUtils utils;
	static SQLiteADFTestUtils sqliteAdfHelper;

	@BeforeClass
	public static void setup() throws Exception {
		folder.create();
		mock = new ComplexDetectionRecordLevelWithVaccinationMock();
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
	public void checkVaccinationDetections() throws Exception {
		/*
			- Street Present, 2 vaccinations CVX recognized -> Not triggered
			- Street Missing, 1 vaccination CVX unrecognized -> Not triggered
			- Street Present, 1 vx CVX recognized, 1 vx CVX unrecognized -> Triggered
		 */
		Dictionaries d = reader.getDictionaries();
		PreparedStatement search = reader.getConnection().prepareStatement("SELECT (count(*) = 1) as found FROM V_DETECTIONS WHERE PROVIDER_ID = ? AND AGE_GROUP = ? AND DETECTION_CODE = ? AND P = ? AND N = ?");
		PreparedStatement count = reader.getConnection().prepareStatement("SELECT count(*) as nb FROM V_DETECTIONS");
		List<List<Integer>> rows = Arrays.asList(
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("ANY".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.DETECTION, Detection.VaccinationCvxCodeIsUnrecognized.getMqeMqeCode()), 3, 2)
		);
		sqliteAdfHelper.checkTableContent(search, count, rows);
	}

	@Test
	public void checkRecordLevelDetections() throws Exception {
		Dictionaries d = reader.getDictionaries();
		PreparedStatement search = reader.getConnection().prepareStatement("SELECT (count(*) = 1) as found FROM P_DETECTIONS WHERE AGE_GROUP = ? AND DETECTION_CODE = ? AND P = ? AND N = ?");
		PreparedStatement count = reader.getConnection().prepareStatement("SELECT count(*) as nb FROM P_DETECTIONS");
		List<List<Integer>> rows = Arrays.asList(
				Arrays.asList(
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.DETECTION, Detection.PatientAddressStreetIsPresent.getMqeMqeCode()), 1, 2),
				Arrays.asList(
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.DETECTION, ComplexDetectionMultiOpMock.DETECTION_CODE), 2, 1)
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
