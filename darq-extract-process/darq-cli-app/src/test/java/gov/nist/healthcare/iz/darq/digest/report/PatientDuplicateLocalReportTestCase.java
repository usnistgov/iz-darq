package gov.nist.healthcare.iz.darq.digest.report;

import gov.nist.healthcare.iz.darq.adf.module.sqlite.SqliteADFReader;
import gov.nist.healthcare.iz.darq.digest.common.CLITestRunnerUtils;
import gov.nist.healthcare.iz.darq.digest.common.SQLiteADFTestUtils;
import gov.nist.healthcare.iz.darq.digest.service.report.instances.DuplicateRecordsReportService;
import gov.nist.healthcare.iz.darq.test.helper.Record;
import gov.nist.healthcare.iz.darq.test.data.mocks.DuplicatePatientExampleMock;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PatientDuplicateLocalReportTestCase {
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
	public void checkReportExists() {
		Path reportPath = utils.getLocalReport(DuplicateRecordsReportService.FILENAME);
		assertTrue(Files.exists(reportPath));
	}


	@Test
	public void checkLocalReportContent() throws Exception {
		String SIGNATURE = "cK9E6EH243DMd14:hKAAQA:hKAAQA:hKAAQA:hKAAQA";
		Path reportPath = utils.getLocalReport(DuplicateRecordsReportService.FILENAME);
		FileReader reader = new FileReader(reportPath.toFile());
		CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
		List<CSVRecord> lines = parser.getRecords();
		assertEquals(3, lines.size()); // Header + 2 duplicates
		List<Record> records = utils.getRecords();

		// Record 2 (index 1) - is duplicate of Record 1 (index 0)
		String recordId = records.get(1).getPatientColumns().get(0);
		String duplicateId = records.get(0).getPatientColumns().get(0);
		assertEquals(lines.get(1).get(0), recordId);
		assertEquals(lines.get(1).get(1), duplicateId);
		assertEquals(lines.get(1).get(2), SIGNATURE);

		// Record 4 (index 3) - is duplicate of Record 3 (index 2)
		String recordId2 = records.get(3).getPatientColumns().get(0);
		String duplicateId2 = records.get(2).getPatientColumns().get(0);
		assertEquals(lines.get(2).get(0), recordId2);
		assertEquals(lines.get(2).get(1), duplicateId2);
		assertEquals(lines.get(2).get(2), SIGNATURE);
	}


	@AfterClass
	public static void close() throws Exception {
		if(reader != null) {
			reader.close();
		}
	}
}
