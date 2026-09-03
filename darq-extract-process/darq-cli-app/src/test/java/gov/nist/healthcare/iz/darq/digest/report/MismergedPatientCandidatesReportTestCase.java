package gov.nist.healthcare.iz.darq.digest.report;

import gov.nist.healthcare.iz.darq.adf.module.sqlite.SqliteADFReader;
import gov.nist.healthcare.iz.darq.digest.common.CLITestRunnerUtils;
import gov.nist.healthcare.iz.darq.digest.common.SQLiteADFTestUtils;
import gov.nist.healthcare.iz.darq.digest.mock.MismergedPatientCandidatesReportMock;
import gov.nist.healthcare.iz.darq.digest.service.report.instances.MismergedPatientCandidatesReportService;
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

public class MismergedPatientCandidatesReportTestCase {

	/*
	 * Report layout, as written by AggregateLocalReportService:
	 *   [0] Patient ID
	 *   [1] More than 5 invalid doses  ("X" or "")
	 *   [2] Too many flu doses         ("X" or "")
	 *   [3] Too many COVID doses       ("X" or "")
	 *   [4] Count                      (aggregate, appended by the base class)
	 * CSVFormat.DEFAULT does not treat the first record as a header, so lines.get(0) is the
	 * header row and lines.size() is 1 + the number of reported patients.
	 */
	private static final int COL_PATIENT_ID = 0;
	private static final int COL_INVALID_DOSES = 1;
	private static final int COL_FLU_DOSES = 2;
	private static final int COL_COVID_DOSES = 3;
	private static final int COL_COUNT = 4;

	private static final String MARKED = "X";
	private static final String NOT_MARKED = "";

	static TemporaryFolder folder = new TemporaryFolder();
	static SqliteADFReader reader;
	static MismergedPatientCandidatesReportMock mock;
	static CLITestRunnerUtils utils;
	static SQLiteADFTestUtils sqliteAdfHelper;

	@BeforeClass
	public static void setup() throws Exception {
		folder.create();
		mock = MismergedPatientCandidatesReportMock.get();
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
		Path reportPath = utils.getLocalReport(MismergedPatientCandidatesReportService.FILENAME);
		assertTrue(Files.exists(reportPath));
	}

	@Test
	public void checkLocalReportContent() throws Exception {
		List<CSVRecord> lines = readReport();

		// TODO set to 1 header + one row per patient expected in the report
		assertEquals(4, lines.size());

		// More than 5 invalid doses -> only column 1 marked
		assertTrue(lines.stream().anyMatch((record) ->
				record.get(COL_PATIENT_ID).equals(mock.invalidDosesPatientId) &&
				record.get(COL_INVALID_DOSES).equals(MARKED) &&
				record.get(COL_FLU_DOSES).equals(NOT_MARKED) &&
				record.get(COL_COVID_DOSES).equals(NOT_MARKED) &&
				record.get(COL_COUNT).equals("1"))
		);

		// Too many flu doses -> only column 2 marked
		assertTrue(lines.stream().anyMatch((record) ->
				record.get(COL_PATIENT_ID).equals(mock.fluDosesPatientId) &&
				record.get(COL_INVALID_DOSES).equals(NOT_MARKED) &&
				record.get(COL_FLU_DOSES).equals(MARKED) &&
				record.get(COL_COVID_DOSES).equals(NOT_MARKED) &&
				record.get(COL_COUNT).equals("1"))
		);

		// Too many COVID doses -> only column 3 marked. Any of the 4/5/6-or-more detections
		// collapses into this single column.
		assertTrue(lines.stream().anyMatch((record) ->
				record.get(COL_PATIENT_ID).equals(mock.covidDosesPatientId) &&
				record.get(COL_INVALID_DOSES).equals(NOT_MARKED) &&
				record.get(COL_FLU_DOSES).equals(NOT_MARKED) &&
				record.get(COL_COVID_DOSES).equals(MARKED) &&
				record.get(COL_COUNT).equals("1"))
		);
	}

	@Test
	public void patientWithoutMismergeSignalIsNotReported() throws Exception {
		List<CSVRecord> lines = readReport();
		assertTrue(lines.stream().noneMatch((record) ->
				record.get(COL_PATIENT_ID).equals(mock.cleanPatientId))
		);
	}

	/**
	 * Guards the off-by-one that is easy to reintroduce: the base class appends the aggregate
	 * count as a trailing column, so getHeader() has to account for it or the header sits one
	 * column short of every data row.
	 */
	@Test
	public void headerMatchesRowWidth() throws Exception {
		List<CSVRecord> lines = readReport();
		assertEquals(COL_COUNT + 1, lines.get(0).size());
		lines.forEach((record) -> assertEquals(COL_COUNT + 1, record.size()));
	}

	private List<CSVRecord> readReport() throws Exception {
		Path reportPath = utils.getLocalReport(MismergedPatientCandidatesReportService.FILENAME);
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