package gov.nist.healthcare.iz.darq.digest.report;

import gov.nist.healthcare.iz.darq.adf.module.sqlite.SqliteADFReader;
import gov.nist.healthcare.iz.darq.digest.common.CLITestRunnerUtils;
import gov.nist.healthcare.iz.darq.digest.common.SQLiteADFTestUtils;
import gov.nist.healthcare.iz.darq.digest.mock.MismergedPatientCandidatesReportMock;
import gov.nist.healthcare.iz.darq.digest.service.report.instances.MismergedPatientCandidatesReportService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * With none of the required detections configured, LocalReportService.dependenciesAreMet is false
 * and the report should not be produced at all.
 */
public class MismergedPatientCandidatesReportNoDependencyTestCase {
	static TemporaryFolder folder = new TemporaryFolder();
	static SqliteADFReader reader;
	static MismergedPatientCandidatesReportMock mock;
	static CLITestRunnerUtils utils;
	static SQLiteADFTestUtils sqliteAdfHelper;

	@BeforeClass
	public static void setup() throws Exception {
		folder.create();
		mock = MismergedPatientCandidatesReportMock.get();
		// Remove required detections
		mock.getConfigurationPayload().setDetections(new ArrayList<>());
		utils = new CLITestRunnerUtils(mock, folder);
		sqliteAdfHelper = new SQLiteADFTestUtils();
		utils.createFiles();
		utils.runCLI();
		reader = sqliteAdfHelper.readADF(utils.getCryptoKey(), folder);
		assertTrue(reader.isReady() && reader.isOpen());
	}

	@Test
	public void configurationPayloadExpectation() {
		assertEquals(mock.getConfigurationPayload().getDetections().size(), 0);
		assertEquals(mock.getConfigurationPayload(), reader.getConfigurationPayload());
	}

	@Test
	public void checkReportDoesNotExists() {
		Path reportPath = utils.getLocalReport(MismergedPatientCandidatesReportService.FILENAME);
		assertFalse(Files.exists(reportPath));
	}

	@AfterClass
	public static void close() throws Exception {
		if(reader != null) {
			reader.close();
		}
	}
}