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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LowercaseMvxReportTestCase {

	/*
	 * Report layout, as written by AggregateLocalReportService:
	 *   [0] MVX, in its ORIGINAL (pre-fix) spelling
	 *   [1] the aggregate count the base class appends
	 * CSVFormat.DEFAULT does not treat the first record as a header, so lines.get(0) is the
	 * header row and lines.size() is 1 + the number of distinct spellings.
	 */
	private static final int COL_MVX = 0;
	private static final int COL_AGGREGATE_COUNT = 1;

	static TemporaryFolder folder = new TemporaryFolder();
	static SqliteADFReader reader;
	static LowercaseMvxReportMock mock;
	static CLITestRunnerUtils utils;
	static SQLiteADFTestUtils sqliteAdfHelper;

	@BeforeClass
	public static void setup() throws Exception {
		folder.create();
		mock = LowercaseMvxReportMock.get();
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
		Path reportPath = utils.getLocalReport(LowercaseMvxReportService.FILENAME);
		assertTrue(Files.exists(reportPath));
	}

	@Test
	public void checkLocalReportContent() throws Exception {
		List<CSVRecord> lines = readReport();
		/*
		msd  all lowercase -> RECORD 1 (once) and RECORD 3 (twice)
		pfr  all lowercase -> RECORD 2 only, twice within that one record
		Skb  mixed case    -> RECORD 3 only
		MSD, PMC           -> already uppercase, never counted
		""                 -> blank, never counted
		 */
		// 1 header + 3 distinct spellings
		assertEquals(4, lines.size());

		assertTrue(lines.stream().anyMatch((record) ->
				record.get(COL_MVX).equals(mock.mixedCaseGsk) &&
				record.get(COL_AGGREGATE_COUNT).equals("1"))
		);
	}

	/**
	 * The key is the original spelling, so the code the preprocess wrote back into the record
	 * ("MSD") must not appear, and neither must a code that was already uppercase on input.
	 */
	@Test
	public void alreadyUppercaseMvxIsNotReported() throws Exception {
		List<CSVRecord> lines = readReport();
		assertEquals(0, lines.stream()
				.skip(1) // header
				.filter((record) -> record.get(COL_MVX).equals(mock.uppercaseMerck)
						|| record.get(COL_MVX).equals(mock.uppercaseSanofi))
				.count());
	}

	@Test
	public void blankMvxIsNotReported() throws Exception {
		List<CSVRecord> lines = readReport();
		assertTrue(lines.stream()
				.skip(1) // header
				.noneMatch((record) -> record.get(COL_MVX).trim().isEmpty()));
	}

	/**
	 * Mixed case counts as "not fully uppercased" just like all lowercase does, and "Skb" is a
	 * distinct key from a hypothetical "skb" - the report index is case sensitive.
	 */
	@Test
	public void mixedCaseMvxIsReportedOnItsOwnRow() throws Exception {
		List<CSVRecord> lines = readReport();
		assertEquals(1, lines.stream()
				.filter((record) -> record.get(COL_MVX).equals(mock.mixedCaseGsk))
				.count());
	}

	/**
	 * The same spelling seen in two different records aggregates onto one row, and the count is
	 * the number of records that contained it.
	 */
	@Test
	public void sameSpellingAcrossRecordsAggregatesIntoOneRow() throws Exception {
		List<CSVRecord> lines = readReport();

		assertEquals(1, lines.stream()
				.filter((record) -> record.get(COL_MVX).equals(mock.lowercaseMerck))
				.count());

		// RECORD 1 (one vaccination) and RECORD 3 (two vaccinations) -> 2 records.
		assertTrue(lines.stream().anyMatch((record) ->
				record.get(COL_MVX).equals(mock.lowercaseMerck) &&
				record.get(COL_AGGREGATE_COUNT).equals("2"))
		);
	}

	/**
	 * Documents current behaviour rather than endorsing it. LowercaseMvxReportService emits one
	 * row per distinct spelling found in a record and drops the per-record occurrence count it
	 * has in hand, so the aggregate counts records, not occurrences. "pfr" appears on two
	 * vaccinations of RECORD 2 and yet reports 1.
	 */
	@Test
	public void countIsPerRecordNotPerOccurrence() throws Exception {
		List<CSVRecord> lines = readReport();
		CSVRecord pfizer = lines.stream()
				.filter((record) -> record.get(COL_MVX).equals(mock.lowercasePfizer))
				.findFirst()
				.orElseThrow(() -> new AssertionError("expected a row for " + mock.lowercasePfizer));
		assertEquals("1", pfizer.get(COL_AGGREGATE_COUNT));
	}

	/**
	 * The base class appends the aggregate count as a trailing column, so the declared header
	 * ("MVX", "Count") lines up with the data rows at two columns.
	 */
	@Test
	public void headerMatchesDataRowWidth() throws Exception {
		List<CSVRecord> lines = readReport();
		assertEquals(2, lines.get(0).size());
		lines.stream().skip(1).forEach((record) -> assertEquals(2, record.size()));
	}

	private List<CSVRecord> readReport() throws Exception {
		Path reportPath = utils.getLocalReport(LowercaseMvxReportService.FILENAME);
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