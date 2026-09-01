package gov.nist.healthcare.iz.darq.digest.report;

import gov.nist.healthcare.iz.darq.adf.module.sqlite.SqliteADFReader;
import gov.nist.healthcare.iz.darq.digest.common.CLITestRunnerUtils;
import gov.nist.healthcare.iz.darq.digest.common.SQLiteADFTestUtils;
import gov.nist.healthcare.iz.darq.digest.mock.BadPhoneNumberReportMock;
import gov.nist.healthcare.iz.darq.digest.service.report.instances.BadPhoneNumberReportService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.immregistries.mqe.validator.detection.Detection;
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

public class BadPhoneNumberReportTestCase {
	static TemporaryFolder folder = new TemporaryFolder();
	static SqliteADFReader reader;
	static BadPhoneNumberReportMock mock;
	static CLITestRunnerUtils utils;
	static SQLiteADFTestUtils sqliteAdfHelper;

	@BeforeClass
	public static void setup() throws Exception {
		folder.create();
		mock = BadPhoneNumberReportMock.get();
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
		Path reportPath = utils.getLocalReport(BadPhoneNumberReportService.FILENAME);
		assertTrue(Files.exists(reportPath));}

	@Test
	public void checkLocalReportContent() throws Exception {
		Path reportPath = utils.getLocalReport(BadPhoneNumberReportService.FILENAME);
		FileReader reader = new FileReader(reportPath.toFile());
		CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
		List<CSVRecord> lines = parser.getRecords();
		/*
		800-544-5555 (Valid) => not in file
		800-555-12121 (Invalid) x 2
		555-1212 (Invalid) x 1
		800-555-ABCD (Invalid) x 2
		000-555-1212 (Invalid) x 1
		"" (Empty) => not in file
		 */
		assertEquals(5, lines.size());
		// 800-555-12121 x 2
		assertTrue(lines.stream().anyMatch((record) -> record.get(0).equals(mock.invalidPhoneLong) &&
				record.get(1).contains(Detection.PatientPhoneIsInvalid.getMqeMqeCode()) &&
				record.get(2).equals("2"))
		);
		// 555-1212 x 1
		assertTrue(lines.stream().anyMatch((record) -> record.get(0).equals(mock.invalidPhoneShort) &&
				record.get(1).contains(Detection.PatientPhoneIsInvalid.getMqeMqeCode()) &&
				record.get(2).equals("1"))
		);
		// 800-555-ABCD x 2
		assertTrue(lines.stream().anyMatch((record) -> record.get(0).equals(mock.invalidPhoneFormat) &&
				record.get(1).contains(Detection.PatientPhoneIsInvalid.getMqeMqeCode()) &&
				record.get(2).equals("2"))
		);
		// 000-555-1212 x 1
		assertTrue(lines.stream().anyMatch((record) -> record.get(0).equals(mock.invalidPhoneArea) &&
				record.get(1).contains(Detection.PatientPhoneIsInvalid.getMqeMqeCode()) &&
				record.get(2).equals("1"))
		);
	}


	@AfterClass
	public static void close() throws Exception {
		if(reader != null) {
			reader.close();
		}
	}
}
