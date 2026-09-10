package gov.nist.healthcare.iz.darq.digest.report;

import gov.nist.healthcare.iz.darq.adf.module.sqlite.SqliteADFReader;
import gov.nist.healthcare.iz.darq.digest.common.CLITestRunnerUtils;
import gov.nist.healthcare.iz.darq.digest.common.SQLiteADFTestUtils;
import gov.nist.healthcare.iz.darq.digest.mock.VaccineCodeReportMock;
import gov.nist.healthcare.iz.darq.digest.service.report.instances.VaccineCodeReportService;
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

public class VaccineCodeReportTestCase {

	/*
	 * Report layout, as written by AggregateLocalReportService:
	 *   [0] CVX
	 *   [1] the per-record count the service puts in the row
	 *   [2] the aggregate count the base class appends
	 * CSVFormat.DEFAULT does not treat the first record as a header, so lines.get(0) is the
	 * header row and lines.size() is 1 + the number of distinct codes.
	 */
	private static final int COL_CVX = 0;
	private static final int COL_ROW_COUNT = 1;
	private static final int COL_AGGREGATE_COUNT = 2;

	static TemporaryFolder folder = new TemporaryFolder();
	static SqliteADFReader reader;
	static VaccineCodeReportMock mock;
	static CLITestRunnerUtils utils;
	static SQLiteADFTestUtils sqliteAdfHelper;

	@BeforeClass
	public static void setup() throws Exception {
		folder.create();
		mock = VaccineCodeReportMock.get();
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
		Path reportPath = utils.getLocalReport(VaccineCodeReportService.FILENAME);
		assertTrue(Files.exists(reportPath));
	}

	@Test
	public void checkLocalReportContent() throws Exception {
		List<CSVRecord> lines = readReport();
		/*
		08  Hep B ped  -> in RECORD 1, RECORD 2 and RECORD 3 (as "08", "08" x2, "8" + "008")
		20  DTaP       -> RECORD 1 only
		140 Influenza  -> RECORD 3 only
		03  MMR        -> RECORD 4 only
		""             -> blank, never counted
		 */
		// 1 header + 4 distinct codes
		assertEquals(5, lines.size());

		assertTrue(lines.stream().anyMatch((record) ->
				record.get(COL_CVX).equals(mock.cvxDtap) &&
				record.get(COL_AGGREGATE_COUNT).equals("1"))
		);
		assertTrue(lines.stream().anyMatch((record) ->
				record.get(COL_CVX).equals(mock.cvxInfluenza) &&
				record.get(COL_AGGREGATE_COUNT).equals("1"))
		);
		assertTrue(lines.stream().anyMatch((record) ->
				record.get(COL_CVX).equals(mock.cvxMmr) &&
				record.get(COL_AGGREGATE_COUNT).equals("1"))
		);
	}

	/**
	 * "8" and "008" are rewritten to "08" by CVXFieldTransformation before the count is taken, so
	 * all three spellings land on one row rather than three.
	 */
	@Test
	public void equivalentCvxSpellingsAggregateIntoOneRow() throws Exception {
		List<CSVRecord> lines = readReport();

		assertEquals(0, lines.stream()
				.filter((record) -> record.get(COL_CVX).equals(mock.cvxHepBPedBare)
						|| record.get(COL_CVX).equals(mock.cvxHepBPedPadded))
				.count());

		assertEquals(1, lines.stream()
				.filter((record) -> record.get(COL_CVX).equals(mock.cvxHepBPedNormalised))
				.count());

		// Seen in RECORD 1, RECORD 2 and RECORD 3.
		assertTrue(lines.stream().anyMatch((record) ->
				record.get(COL_CVX).equals(mock.cvxHepBPedNormalised) &&
				record.get(COL_AGGREGATE_COUNT).equals("3"))
		);
	}

	@Test
	public void blankVaccineCodeIsNotReported() throws Exception {
		List<CSVRecord> lines = readReport();
		assertTrue(lines.stream()
				.skip(1) // header
				.noneMatch((record) -> record.get(COL_CVX).trim().isEmpty()));
	}

	/**
	 * Documents current behaviour rather than endorsing it. The service writes its own per-record
	 * count into the row while AggregateLocalReportService appends an aggregate count, and
	 * ROW_VALUE is only stored on the first insert ("ON CONFLICT DO UPDATE SET N=N+?"), so the
	 * first column of counts freezes at whatever the first contributing record had. Code "08"
	 * occurs 5 times overall (1 + 2 + 2) across 3 records, yet reports 1 and 3 - neither is the
	 * total. See the note on this test if that count is ever made meaningful.
	 */
	@Test
	public void perRecordCountColumnReflectsOnlyTheFirstContributingRecord() throws Exception {
		List<CSVRecord> lines = readReport();
		CSVRecord hepB = lines.stream()
				.filter((record) -> record.get(COL_CVX).equals(mock.cvxHepBPedNormalised))
				.findFirst()
				.orElseThrow(() -> new AssertionError("expected a row for " + mock.cvxHepBPedNormalised));

		assertEquals("1", hepB.get(COL_ROW_COUNT));
		assertEquals("3", hepB.get(COL_AGGREGATE_COUNT));
	}

	/**
	 * The base class appends the aggregate count as a trailing column, so every data row is one
	 * column wider than getHeader() declares.
	 */
	@Test
	public void headerIsOneColumnNarrowerThanDataRows() throws Exception {
		List<CSVRecord> lines = readReport();
		assertEquals(2, lines.get(0).size());
		lines.stream().skip(1).forEach((record) -> assertEquals(3, record.size()));
	}

	private List<CSVRecord> readReport() throws Exception {
		Path reportPath = utils.getLocalReport(VaccineCodeReportService.FILENAME);
		FileReader fileReader = new FileReader(reportPath.toFile());
		CSVParser parser = new CSVParser(fileReader, CSVFormat.DEFAULT);
		return parser.getRecords();
	}

	@AfterClass
	public static void close() throws Exception {
		if(reader != null) {
			reader.close();
		}
	}
}
