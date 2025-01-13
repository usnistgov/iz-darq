package gov.nist.healthcare.iz.darq.digest.detections;

import gov.nist.healthcare.iz.darq.adf.module.sqlite.SqliteADFReader;
import gov.nist.healthcare.iz.darq.adf.module.sqlite.model.Dictionaries;
import gov.nist.healthcare.iz.darq.digest.common.CLITestRunnerUtils;
import gov.nist.healthcare.iz.darq.digest.common.SQLiteADFTestUtils;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.mock.complex.ComplexDetectionDenominatorMock;
import gov.nist.healthcare.iz.darq.digest.mock.complex.ComplexDetectionVaccinationLevelMock;
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

public class ComplexDetectionVaccinationLevelTestCase {
	static TemporaryFolder folder = new TemporaryFolder();
	static SqliteADFReader reader;
	static ComplexDetectionVaccinationLevelMock mock;
	static CLITestRunnerUtils utils;
	static SQLiteADFTestUtils sqliteAdfHelper;

	@BeforeClass
	public static void setup() throws Exception {
		folder.create();
		mock = new ComplexDetectionVaccinationLevelMock();
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
/*
	Test about Vaccination Level Detection
		-> Vaccine Lot Number is missing OR Vaccine CVX is missing => Should be only checked on administered
		-> Record with 4 vaccines, 4 Administered, All records are missing Lot Number and CVX, found should be = 4
 */
		Dictionaries d = reader.getDictionaries();
		PreparedStatement search = reader.getConnection().prepareStatement("SELECT (count(*) = 1) as found FROM V_DETECTIONS WHERE PROVIDER_ID = ? AND AGE_GROUP = ? AND DETECTION_CODE = ? AND P = ? AND N = ?");
		PreparedStatement count = reader.getConnection().prepareStatement("SELECT count(*) as nb FROM V_DETECTIONS");
		List<List<Integer>> rows = Arrays.asList(
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("ANY".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.DETECTION, ComplexDetectionDenominatorMock.DETECTION_CODE), 0, 4),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("ANY".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.DETECTION, Detection.VaccinationLotNumberIsMissing.getMqeMqeCode()), 0, 4),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("ANY".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.DETECTION, Detection.VaccinationCvxCodeIsMissing.getMqeMqeCode()), 0, 4)
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
