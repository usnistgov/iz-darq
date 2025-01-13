package gov.nist.healthcare.iz.darq.digest.report;

import gov.nist.healthcare.iz.darq.adf.module.sqlite.SqliteADFReader;
import gov.nist.healthcare.iz.darq.digest.common.CLITestRunnerUtils;
import gov.nist.healthcare.iz.darq.digest.common.SQLiteADFTestUtils;
import gov.nist.healthcare.iz.darq.digest.mock.PlaceholderNamesMock;
import gov.nist.healthcare.iz.darq.digest.service.report.instances.PlaceholderNameReportService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.immregistries.mqe.validator.engine.codes.KnownName;
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
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PlaceholderNameLocalReportTestCase {
	static TemporaryFolder folder = new TemporaryFolder();
	static SqliteADFReader reader;
	static PlaceholderNamesMock mock;
	static CLITestRunnerUtils utils;
	static SQLiteADFTestUtils sqliteAdfHelper;
	static Map<KnownName, Integer> names;

	@BeforeClass
	public static void setup() throws Exception {
		folder.create();
		mock = PlaceholderNamesMock.get();
		utils = new CLITestRunnerUtils(mock, folder);
		sqliteAdfHelper = new SQLiteADFTestUtils();
		utils.createFiles();
		names = mock.names;
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
		Path reportPath = utils.getLocalReport(PlaceholderNameReportService.FILENAME);
		assertTrue(Files.exists(reportPath));
	}

	@Test
	public void checkLocalReportContent() throws Exception {
		Path reportPath = utils.getLocalReport(PlaceholderNameReportService.FILENAME);
		FileReader reader = new FileReader(reportPath.toFile());
		CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
		List<CSVRecord> lines = parser.getRecords();
		int size = names.size() - 1; // BABY - Junk and BABY - Newborn are merged
		names.put(
				new KnownName(24, "BOY", "BABY", "", null, KnownName.NameType.UNNAMED_NEWBORN),
				3
		);
		names.put(
				new KnownName(26, "MOUSE", "MICKY", "", null, KnownName.NameType.TEST_PATIENT),
				1
		);
		names.put(
				new KnownName(53, "", "NOFIRSTNAME", "", null, KnownName.NameType.JUNK_NAME),
				4
		);
		names.put(
				new KnownName(74, "", "BABY", "", null, KnownName.NameType.JUNK_NAME),
				2
		);
		names.put(
				new KnownName(22, "", "BABY", "", null, KnownName.NameType.UNNAMED_NEWBORN),
				4
		);
		assertEquals(size + 1, lines.size());
		assertTrue(lines.stream().anyMatch((record) ->
				                                   record.get(0).equals("BABY") &&
						                                   record.get(1).equals("") &&
						                                   record.get(2).equals("BOY") &&
						                                   record.get(4).equals("3") &&
						                                   record.get(3).contains(KnownName.NameType.UNNAMED_NEWBORN.name())
		           )
		);
		assertTrue(lines.stream().anyMatch((record) ->
				                                   record.get(0).equals("MICKY") &&
						                                   record.get(1).equals("") &&
						                                   record.get(2).equals("MOUSE") &&
						                                   record.get(4).equals("1") &&
						                                   record.get(3).contains(KnownName.NameType.TEST_PATIENT.name())
		           )
		);
		assertTrue(lines.stream().anyMatch((record) ->
				                                   record.get(0).equals("NOFIRSTNAME") &&
						                                   record.get(1).equals("") &&
						                                   record.get(2).equals("") &&
						                                   record.get(4).equals("4") &&
						                                   record.get(3).contains(KnownName.NameType.JUNK_NAME.name())
		           )
		);
		assertTrue(lines.stream().anyMatch((record) ->
				                                   record.get(0).equals("BABY") &&
						                                   record.get(1).equals("") &&
						                                   record.get(2).equals("") &&
						                                   record.get(4).equals("9") && // Match all BABY _ BOY (JUNK) and Match BABY _ _ (JUNK, NEWBORN)
						                                   record.get(3).contains(KnownName.NameType.UNNAMED_NEWBORN.name()) &&
						                                   record.get(3).contains(KnownName.NameType.JUNK_NAME.name())
		           )
		);
	}


	@AfterClass
	public static void close() throws Exception {
		if(reader != null) {
			reader.close();
		}
	}
}
