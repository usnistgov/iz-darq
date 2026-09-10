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
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Drives the SME-supplied extract (src/test/resources/mismerged) through the CLI and checks each
 * cohort lands in the column it was designed for.
 */
public class MismergedPatientCandidatesReportTestCase {

	/*
	 * Report layout, as written by AggregateLocalReportService:
	 *   [0] Patient ID
	 *   [1] More than 5 invalid doses  ("X" or "")
	 *   [2] Too many flu doses         ("X" or "")
	 *   [3] Too many COVID doses       ("X" or "")
	 *   [4] High volume immunizations  ("X" or "")   MQE0568
	 *   [5] Count                      (aggregate, appended by the base class)
	 * CSVFormat.DEFAULT does not treat the first record as a header, so lines.get(0) is the
	 * header row and lines.size() is 1 + the number of reported patients.
	 */
	private static final int COL_PATIENT_ID = 0;
	private static final int COL_INVALID_DOSES = 1;
	private static final int COL_FLU_DOSES = 2;
	private static final int COL_COVID_DOSES = 3;
	private static final int COL_HIGH_VOLUME = 4;
	private static final int COL_COUNT = 5;

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
	public void everyFlaggedCohortIsReportedAndControlsAreNot() throws Exception {
		List<CSVRecord> lines = readReport();
		// 1 header + the 12 flagged patients. The 3 controls are below every threshold.
		assertEquals(13, lines.size());
		mock.controlPatients.forEach((patient) ->
				assertTrue("control patient " + patient + " must not be reported",
						lines.stream().noneMatch((record) -> record.get(COL_PATIENT_ID).equals(patient)))
		);
	}

	/** 101, 102, 103 - six doses of a single vaccine days apart, evaluated as 5+ invalid. */
	@Test
	public void invalidDoseCohortMarksOnlyTheInvalidDosesColumn() throws Exception {
		for (String patient : mock.invalidDosesPatients) {
			assertColumns(patient, MARKED, NOT_MARKED, NOT_MARKED, NOT_MARKED);
		}
	}

	/** 301, 302, 303 - two flu doses inside the Sep 2021 - Mar 2022 season, patients aged over 10. */
	@Test
	public void fluCohortMarksOnlyTheFluColumn() throws Exception {
		for (String patient : mock.fluDosesPatients) {
			assertColumns(patient, NOT_MARKED, MARKED, NOT_MARKED, NOT_MARKED);
		}
	}

	/** 201, 202, 203 - four COVID doses (CVX 207/208) administered during calendar year 2021. */
	@Test
	public void covidCohortMarksOnlyTheCovidColumn() throws Exception {
		for (String patient : mock.covidDosesPatients) {
			assertColumns(patient, NOT_MARKED, NOT_MARKED, MARKED, NOT_MARKED);
		}
	}

	/**
	 * 401, 402, 403 - MQE0568. 401 carries 21 doses before six months of age (the >= 20 infant
	 * branch) and 402/403 carry 31 before two years (the >= 30 toddler branch). They are also
	 * marked for invalid doses, which is expected rather than incidental: administering that many
	 * doses to an infant necessarily produces invalid ones too.
	 */
	@Test
	public void highVolumeCohortMarksHighVolumeAlongsideInvalidDoses() throws Exception {
		for (String patient : mock.highVolumePatients) {
			assertColumns(patient, MARKED, NOT_MARKED, NOT_MARKED, MARKED);
		}
	}

	/**
	 * The base class appends the aggregate count as a trailing column, so getHeader() has to
	 * account for it or the header sits one column short of every data row.
	 */
	@Test
	public void headerMatchesRowWidth() throws Exception {
		List<CSVRecord> lines = readReport();
		assertEquals(COL_COUNT + 1, lines.get(0).size());
		lines.forEach((record) -> assertEquals(COL_COUNT + 1, record.size()));
	}

	private void assertColumns(String patient, String invalidDoses, String flu, String covid, String highVolume)
			throws Exception {
		CSVRecord record = rowFor(patient);
		assertEquals(patient + " invalid doses", invalidDoses, record.get(COL_INVALID_DOSES));
		assertEquals(patient + " flu doses", flu, record.get(COL_FLU_DOSES));
		assertEquals(patient + " covid doses", covid, record.get(COL_COVID_DOSES));
		assertEquals(patient + " high volume", highVolume, record.get(COL_HIGH_VOLUME));
		assertEquals(patient + " count", "1", record.get(COL_COUNT));
	}

	private CSVRecord rowFor(String patient) throws Exception {
		Optional<CSVRecord> row = readReport().stream()
				.filter((record) -> record.get(COL_PATIENT_ID).equals(patient))
				.findFirst();
		assertTrue("expected a report row for patient " + patient, row.isPresent());
		return row.get();
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
