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
import java.util.*;

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
/*
	RECORD 1
		V1
		v2 -> Duplicate of V1
		v3
	RECORD 2
		V1
		V2
		V3 -> duplicate of v2
		V4 -> duplicate of v3, v2
	RECORD 3
		V1
		V2
 */
	@Test
	public void checkLocalReportContent() throws Exception {
		Path reportPath = utils.getLocalReport(DuplicateVaccinationReportService.FILENAME);
		FileReader reader = new FileReader(reportPath.toFile());
		CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
		List<CSVRecord> lines = parser.getRecords();
		assertEquals(5, lines.size());
		List<Record> records = utils.getRecords();
		checkLineMatch(
				records.get(0),
				lines,
				1,
				new HashSet<>(Collections.singletonList(0))
		);
		checkLineMatch(
				records.get(1),
				lines,
				2,
				new HashSet<>(Collections.singletonList(1))
		);
		checkLineMatch(
				records.get(1),
				lines,
				3,
				new HashSet<>(Arrays.asList(1, 2))
		);
	}

	public void checkLineMatch(
			Record record,
			List<CSVRecord> lines,
			int vaccine,
			Set<Integer> duplicates
	) {
		String recordId = record.getPatientColumns().get(0);
		String vaccineId = record.getVaccinationsColumns().get(vaccine).get(1);
		for(int duplicate : duplicates) {
			String duplicateId = record.getVaccinationsColumns().get(duplicate).get(1);
			assertTrue(lines.stream().anyMatch(line -> line.get(0).equals(recordId) && line.get(1).equals(vaccineId) && line.get(2).equals(duplicateId)));
		}
	}

	@AfterClass
	public static void close() throws Exception {
		if(reader != null) {
			reader.close();
		}
	}
}
