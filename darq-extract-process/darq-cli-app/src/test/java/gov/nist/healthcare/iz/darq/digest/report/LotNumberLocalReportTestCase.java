package gov.nist.healthcare.iz.darq.digest.report;

import gov.nist.healthcare.iz.darq.adf.module.sqlite.SqliteADFReader;
import gov.nist.healthcare.iz.darq.digest.common.CLITestRunnerUtils;
import gov.nist.healthcare.iz.darq.digest.common.SQLiteADFTestUtils;
import gov.nist.healthcare.iz.darq.digest.mock.LotNumberReportMock;
import gov.nist.healthcare.iz.darq.digest.service.report.instances.LotNumberReportService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.immregistries.mqe.validator.detection.Detection;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LotNumberLocalReportTestCase {
	static TemporaryFolder folder = new TemporaryFolder();
	static SqliteADFReader reader;
	static LotNumberReportMock mock;
	static CLITestRunnerUtils utils;
	static SQLiteADFTestUtils sqliteAdfHelper;

	@BeforeClass
	public static void setup() throws Exception {
		folder.create();
		mock = LotNumberReportMock.get();
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
		Path reportPath = utils.getLocalReport(LotNumberReportService.FILENAME);
		assertTrue(Files.exists(reportPath));
	}

	@Test
	public void checkLocalReportContent() throws Exception {
		Path reportPath = utils.getLocalReport(LotNumberReportService.FILENAME);
		FileReader reader = new FileReader(reportPath.toFile());
		CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
		List<CSVRecord> lines = parser.getRecords();
		/*
		RECORD 1
		VX 1 => 08 VALID
		VX 2 => 145 VALID
		VX 3 => 08 INVALID
		VX 4 => 145 INVALID
		VX 5 => 08 PREFIX
		RECORD 2
		VX 1 => 08 SUFFIX
		VX 2 => 145 PREFIX
		VX 3 => 08 VALID
		VX 4 => 08 PREFIX
		VX 5 => 145 INFIX
		RECORD 3
		VX 1 => 08 VALID | historical 01
		VX 2 => 145 INVALID | historical 01

		===
		08 VALID 2
		08 VALID 1 - Historical (no validation info)
		145 VALID 1
		08 INVALID 1
		145 INVALID 1
		145 INVALID 1 - Historical (no validation info)
		08 PREFIX 2
		08 SUFFIX 1
		145 PREFIX 1
		145 INFIX 1
		 */
		assertEquals(11, lines.size());
		// 08 VALID 2
		assertTrue(lines.stream().anyMatch((record) -> record.get(0).equals(mock.validLotNumber) &&
				record.get(1).equals("08") &&
				record.get(2).equals("Administered") &&
				record.get(3).isEmpty() &&
				record.get(4).equals("2"))
		);
		// 08 VALID 1 - Historical (no validation info)
		assertTrue(lines.stream().anyMatch((record) -> record.get(0).equals(mock.validLotNumber) &&
				record.get(1).equals("08") &&
				record.get(2).equals("Historical") &&
				record.get(3).isEmpty() &&
				record.get(4).equals("1"))
		);
		// 145 VALID 1
		assertTrue(lines.stream().anyMatch((record) -> record.get(0).equals(mock.validLotNumber) &&
				record.get(1).equals("145") &&
				record.get(2).equals("Administered") &&
				record.get(3).isEmpty() &&
				record.get(4).equals("1"))
		);
		// 08 INVALID 1
		assertTrue(lines.stream().anyMatch((record) -> record.get(0).equals(mock.lotNumberInvalid) &&
				record.get(1).equals("08") &&
				record.get(2).equals("Administered") &&
				record.get(3).contains(Detection.VaccinationLotNumberIsInvalid.getMqeMqeCode()) &&
				record.get(4).equals("1"))
		);
		// 145 INVALID 1
		assertTrue(lines.stream().anyMatch((record) -> record.get(0).equals(mock.lotNumberInvalid) &&
				record.get(1).equals("145") &&
				record.get(2).equals("Administered") &&
				record.get(3).contains(Detection.VaccinationLotNumberIsInvalid.getMqeMqeCode()) &&
				record.get(4).equals("1"))
		);
		// 145 INVALID 1 - Historical (no validation info)
		assertTrue(lines.stream().anyMatch((record) -> record.get(0).equals(mock.lotNumberInvalid) &&
				record.get(1).equals("145") &&
				record.get(2).equals("Historical") &&
				record.get(3).isEmpty() &&
				record.get(4).equals("1"))
		);
		// 08 PREFIX 2
		assertTrue(lines.stream().anyMatch((record) -> record.get(0).equals(mock.prefix) &&
				record.get(1).equals("08") &&
				record.get(2).equals("Administered") &&
				record.get(3).contains(Detection.VaccinationLotNumberHasInvalidPrefixes.getMqeMqeCode()) &&
				record.get(4).equals("2"))
		);
		// 08 SUFFIX 1
		assertTrue(lines.stream().anyMatch((record) -> record.get(0).equals(mock.suffix) &&
				record.get(1).equals("08") &&
				record.get(2).equals("Administered") &&
				record.get(3).contains(Detection.VaccinationLotNumberHasInvalidSuffixes.getMqeMqeCode()) &&
				record.get(4).equals("1"))
		);
		// 145 PREFIX 1
		assertTrue(lines.stream().anyMatch((record) -> record.get(0).equals(mock.prefix) &&
				record.get(1).equals("145") &&
				record.get(2).equals("Administered") &&
				record.get(3).contains(Detection.VaccinationLotNumberHasInvalidPrefixes.getMqeMqeCode()) &&
				record.get(4).equals("1"))
		);
		// 145 INFIX 1
		assertTrue(lines.stream().anyMatch((record) -> record.get(0).equals(mock.infix) &&
				record.get(1).equals("145") &&
				record.get(2).equals("Administered") &&
				record.get(3).contains(Detection.VaccinationLotNumberHasInvalidInfixes.getMqeMqeCode()) &&
				record.get(4).equals("1"))
		);
	}


	@AfterClass
	public static void close() throws Exception {
		if(reader != null) {
			reader.close();
		}
	}
}
