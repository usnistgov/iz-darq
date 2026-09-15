package gov.nist.healthcare.iz.darq.digest.report;

import gov.nist.healthcare.iz.darq.adf.module.sqlite.SqliteADFReader;
import gov.nist.healthcare.iz.darq.digest.common.CLITestRunnerUtils;
import gov.nist.healthcare.iz.darq.digest.common.SQLiteADFTestUtils;
import gov.nist.healthcare.iz.darq.digest.mock.LowercaseMvxReportMock;
import gov.nist.healthcare.iz.darq.digest.service.report.instances.LowercaseMvxReportService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * LowercaseMvxReportService declares no required detections, and the casing fix happens in
 * SimpleDigestRunner's preprocess before any detection runs. So unlike the detection-backed
 * reports, clearing the configured detections must NOT suppress this report - it is still
 * produced, with the same content.
 */
public class LowercaseMvxReportNoDetectionsTestCase {
	static TemporaryFolder folder = new TemporaryFolder();
	static SqliteADFReader reader;
	static LowercaseMvxReportMock mock;
	static CLITestRunnerUtils utils;
	static SQLiteADFTestUtils sqliteAdfHelper;

	@BeforeClass
	public static void setup() throws Exception {
		folder.create();
		mock = LowercaseMvxReportMock.get();
		// Remove every configured detection
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
		assertEquals(0, mock.getConfigurationPayload().getDetections().size());
		assertEquals(mock.getConfigurationPayload(), reader.getConfigurationPayload());
	}

	@Test
	public void checkReportIsStillProduced() {
		Path reportPath = utils.getLocalReport(LowercaseMvxReportService.FILENAME);
		assertTrue(Files.exists(reportPath));
	}

	@Test
	public void codesAreStillCountedWithoutAnyDetection() throws Exception {
		Path reportPath = utils.getLocalReport(LowercaseMvxReportService.FILENAME);
		CSVParser parser = new CSVParser(new FileReader(reportPath.toFile()), CSVFormat.DEFAULT);
		List<CSVRecord> lines = parser.getRecords();

		// 1 header + the same 3 distinct spellings as the detection-configured run
		assertEquals(4, lines.size());
		assertTrue(lines.stream().anyMatch((record) ->
				record.get(0).equals(mock.lowercaseMerck) && record.get(1).equals("2")));
	}

	@AfterClass
	public static void close() throws Exception {
		if(reader != null) {
			reader.close();
		}
	}
}