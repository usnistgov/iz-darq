package gov.nist.healthcare.iz.darq.digest.report;

import gov.nist.healthcare.iz.darq.adf.module.sqlite.SqliteADFReader;
import gov.nist.healthcare.iz.darq.digest.common.CLITestRunnerUtils;
import gov.nist.healthcare.iz.darq.digest.common.SQLiteADFTestUtils;
import gov.nist.healthcare.iz.darq.digest.mock.BadZipCodeReportMock;
import gov.nist.healthcare.iz.darq.digest.service.report.instances.BadZipCodeReportService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.immregistries.mqe.validator.detection.Detection;
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

public class BadZipCodeReportTestCase {
	static TemporaryFolder folder = new TemporaryFolder();
	static SqliteADFReader reader;
	static BadZipCodeReportMock mock;
	static CLITestRunnerUtils utils;
	static SQLiteADFTestUtils sqliteAdfHelper;

	@BeforeClass
	public static void setup() throws Exception {
		folder.create();
		mock = BadZipCodeReportMock.get();
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
		Path reportPath = utils.getLocalReport(BadZipCodeReportService.FILENAME);
		assertTrue(Files.exists(reportPath));
	}

	@Test
	public void checkLocalReportContent() throws Exception {
		Path reportPath = utils.getLocalReport(BadZipCodeReportService.FILENAME);
		FileReader reader = new FileReader(reportPath.toFile());
		CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
		List<CSVRecord> lines = parser.getRecords();
		/*
		20899  (Valid)                            => not in file
		1234   (Invalid - length != 5)        x 1
		208999 (Invalid - length != 5)        x 1
		00000  (Invalid - repeated digits)    x 1
		99999  (Invalid - repeated digits)    x 1
		00100  (Invalid - lower than 00501)   x 1
		99960  (Invalid - greater than 99950) x 1
		""     (Empty)                            => not in file
		 */
		// CSVFormat.DEFAULT does not treat the first record as a header, so it is parsed as a
		// record too: 1 header line + 6 aggregated invalid-zip rows.
		assertEquals(7, lines.size());

		// 1234 x 1 -- length not equal to 5
		assertTrue(lines.stream().anyMatch((record) -> record.get(0).equals(mock.invalidZipTooShort) &&
				record.get(1).contains(Detection.PatientAddressZipIsInvalid.getMqeMqeCode()) &&
				record.get(2).equals("1"))
		);

		// 208999 x 1 -- length not equal to 5
		assertTrue(lines.stream().anyMatch((record) -> record.get(0).equals(mock.invalidZipTooLong) &&
				record.get(1).contains(Detection.PatientAddressZipIsInvalid.getMqeMqeCode()) &&
				record.get(2).equals("1"))
		);

		// 00000 x 1 -- repeated digits
		assertTrue(lines.stream().anyMatch((record) -> record.get(0).equals(mock.invalidZipRepeatedLow) &&
				record.get(1).contains(Detection.PatientAddressZipIsInvalid.getMqeMqeCode()) &&
				record.get(2).equals("1"))
		);

		// 99999 x 1 -- repeated digits
		assertTrue(lines.stream().anyMatch((record) -> record.get(0).equals(mock.invalidZipRepeatedHigh) &&
				record.get(1).contains(Detection.PatientAddressZipIsInvalid.getMqeMqeCode()) &&
				record.get(2).equals("1"))
		);

		// 00100 x 1 -- lower than 00501
		assertTrue(lines.stream().anyMatch((record) -> record.get(0).equals(mock.invalidZipBelowRange) &&
				record.get(1).contains(Detection.PatientAddressZipIsInvalid.getMqeMqeCode()) &&
				record.get(2).equals("1"))
		);

		// 99960 x 1 -- greater than 99950
		assertTrue(lines.stream().anyMatch((record) -> record.get(0).equals(mock.invalidZipAboveRange) &&
				record.get(1).contains(Detection.PatientAddressZipIsInvalid.getMqeMqeCode()) &&
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
