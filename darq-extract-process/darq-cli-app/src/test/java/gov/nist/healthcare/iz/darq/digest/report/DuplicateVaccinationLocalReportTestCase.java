package gov.nist.healthcare.iz.darq.digest.report;

import gov.nist.healthcare.iz.darq.adf.module.sqlite.SqliteADFReader;
import gov.nist.healthcare.iz.darq.digest.common.CLITestRunnerUtils;
import gov.nist.healthcare.iz.darq.digest.common.SQLiteADFTestUtils;
import gov.nist.healthcare.iz.darq.digest.mock.DuplicateVaccinationMock;
import gov.nist.healthcare.iz.darq.digest.service.report.instances.DuplicateVaccinationReportService;
import gov.nist.healthcare.iz.darq.test.helper.Record;
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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DuplicateVaccinationLocalReportTestCase {
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
	public void checkReportExists() {
		Path reportPath = utils.getLocalReport(DuplicateVaccinationReportService.FILENAME);
		assertTrue(Files.exists(reportPath));
	}


	@Test
	public void checkLocalReportContent() throws Exception {
		Path reportPath = utils.getLocalReport(DuplicateVaccinationReportService.FILENAME);
		FileReader reader = new FileReader(reportPath.toFile());
		CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
		List<CSVRecord> lines = parser.getRecords();
		assertEquals(5, lines.size());
		List<Record> records = utils.getRecords();
		{
			String recordId = records.get(0).getPatientColumns().get(0);
			String vaccineId = records.get(0).getVaccinationsColumns().get(1).get(1);
			String duplicationId = records.get(0).getVaccinationsColumns().get(0).get(1);
			assertEquals(lines.get(1).get(0), recordId);
			assertEquals(lines.get(1).get(1), vaccineId);
			assertEquals(lines.get(1).get(2), duplicationId);
		}
		{
			String recordId = records.get(1).getPatientColumns().get(0);
			String vaccineId = records.get(1).getVaccinationsColumns().get(2).get(1);
			String duplicationId = records.get(1).getVaccinationsColumns().get(1).get(1);
			assertEquals(lines.get(2).get(0), recordId);
			assertEquals(lines.get(2).get(1), vaccineId);
			assertEquals(lines.get(2).get(2), duplicationId);
		}
		{
			String recordId = records.get(1).getPatientColumns().get(0);
			String vaccineId = records.get(1).getVaccinationsColumns().get(3).get(1);
			String duplicationId = records.get(1).getVaccinationsColumns().get(1).get(1);
			assertEquals(lines.get(3).get(0), recordId);
			assertEquals(lines.get(3).get(1), vaccineId);
			assertEquals(lines.get(3).get(2), duplicationId);
		}
		{
			String recordId = records.get(1).getPatientColumns().get(0);
			String vaccineId = records.get(1).getVaccinationsColumns().get(3).get(1);
			String duplicationId = records.get(1).getVaccinationsColumns().get(2).get(1);
			assertEquals(lines.get(4).get(0), recordId);
			assertEquals(lines.get(4).get(1), vaccineId);
			assertEquals(lines.get(4).get(2), duplicationId);
		}
	}

	@AfterClass
	public static void close() throws Exception {
		if(reader != null) {
			reader.close();
		}
	}
}
