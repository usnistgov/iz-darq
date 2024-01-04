package gov.nist.healthcare.iz.darq.digest.adf;

import gov.nist.healthcare.iz.darq.adf.model.Metadata;
import gov.nist.healthcare.iz.darq.adf.module.sqlite.SqliteADFReader;
import gov.nist.healthcare.iz.darq.adf.module.sqlite.model.Dictionaries;
import gov.nist.healthcare.iz.darq.digest.domain.ExtractPercent;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.domain.Summary;
import gov.nist.healthcare.iz.darq.digest.common.CLITestRunnerUtils;
import gov.nist.healthcare.iz.darq.digest.common.SQLiteADFTestUtils;
import gov.nist.healthcare.iz.darq.test.data.mocks.SimpleExampleMock;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SqliteADFContentTestCase {
	static TemporaryFolder folder = new TemporaryFolder();
	static SqliteADFReader reader;
	static SimpleExampleMock mock;
	static CLITestRunnerUtils utils;
	static SQLiteADFTestUtils sqliteAdfHelper;

	@BeforeClass
	public static void setup() throws Exception {
		folder.create();
		mock = SimpleExampleMock.get();
		utils = new CLITestRunnerUtils(mock, folder);
		sqliteAdfHelper = new SQLiteADFTestUtils();
		utils.createFiles();
		utils.runCLI();
		reader = sqliteAdfHelper.readADF(utils.getCryptoKey(), folder);
		assertTrue(reader.isReady() && reader.isOpen());
	}

	@Test
	public void summaryCountExpectations() {
		Summary summary = reader.getSummary();
		assertEquals(4, summary.getCounts().totalReadPatientRecords);
		assertEquals(0, summary.getCounts().totalSkippedPatientRecords);
		assertEquals(9, summary.getCounts().totalReadVaccinations);
		assertEquals(0, summary.getCounts().totalSkippedVaccinationRecords);
		assertEquals(3, summary.getCounts().numberOfProviders);
		assertEquals(1, summary.getCounts().minVaccinationsPerRecord);
		assertEquals(3, summary.getCounts().maxVaccinationsPerRecord);
		assertEquals(7, summary.getCounts().administered);
		assertEquals(2, summary.getCounts().historical);
		assertEquals(null, summary.getIssues());
		assertEquals("12/14/2020", summary.getAsOfDate());
	}

	@Test
	public void ageGroupCountExpectations() {
		Summary summary = reader.getSummary();
		assertTrue(summary.getCountByAgeGroup().stream().anyMatch((ageGroupCount) ->
			ageGroupCount.getNb() == 2 && ageGroupCount.getRange().equals(mock.getAgeGroupHelper().getAgeGroup(0))
		));
		assertTrue(summary.getCountByAgeGroup().stream().anyMatch((ageGroupCount) ->
				ageGroupCount.getNb() == 1 && ageGroupCount.getRange().equals(mock.getAgeGroupHelper().getAgeGroup(1))
		));
		assertTrue(summary.getCountByAgeGroup().stream().anyMatch((ageGroupCount) ->
				ageGroupCount.getNb() == 0 && ageGroupCount.getRange().equals(mock.getAgeGroupHelper().getAgeGroup(2))
		));
		assertEquals(summary.getOutOfRange(), 1);
	}

	@Test
	public void extractCountExpectations() {
		Map<String, ExtractPercent> expected = new HashMap<>();
		ExtractPercent fn = new ExtractPercent();
		ExtractPercent ln = new ExtractPercent();
		ExtractPercent ve = new ExtractPercent();
		ExtractPercent vf = new ExtractPercent();
		ExtractPercent dob = new ExtractPercent();
		ExtractPercent event = new ExtractPercent();
		ExtractPercent cvx = new ExtractPercent();
		ExtractPercent ndc = new ExtractPercent();
		ExtractPercent grp = new ExtractPercent();
		ExtractPercent admin = new ExtractPercent();
		ExtractPercent patId = new ExtractPercent();
		ExtractPercent vaxPatId = new ExtractPercent();
		ExtractPercent vaxId = new ExtractPercent();
		ExtractPercent gender = new ExtractPercent();

		patId.setValued(100);
		patId.setTotal(4);
		expected.put("Patient - Record ID", patId);
		vaxPatId.setValued(100);
		vaxPatId.setTotal(9);
		expected.put("Vaccination - Patient ID", vaxPatId);
		vaxId.setValued(100);
		vaxId.setTotal(9);
		expected.put("Vaccination - Vaccination Event ID", vaxId);
		fn.setValuePresent(50);
		fn.setValueNotPresent(50);
		fn.setTotal(4);
		expected.put("Patient - Name - First Name", fn);
		ln.setValuePresent(50);
		ln.setValueNotPresent(50);
		ln.setTotal(4);
		expected.put("Patient - Name - Last Name", ln);
		ve.setValuePresent((6.0/9) * 100);
		ve.setValueNotPresent((3.0/9) * 100);
		ve.setTotal(9);
		expected.put("Vaccination - Eligibility at Dose", ve);
		vf.setValuePresent((5.0/9) * 100);
		vf.setValueNotPresent((4.0/9) * 100);
		vf.setTotal(9);
		expected.put("Vaccination - Vaccine Funding Source", vf);
		dob.setValued(100);
		dob.setTotal(4);
		expected.put("Patient - Date Of Birth", dob);
		event.setValued(100);
		event.setTotal(9);
		expected.put("Vaccination - Event Information Source", event);
		cvx.setValued(100);
		cvx.setTotal(9);
		expected.put("Vaccination - Vaccine Type CVX", cvx);
		grp.setValued(100);
		grp.setTotal(9);
		expected.put("Vaccination - Reporting Group", grp);
		admin.setValued(100);
		admin.setTotal(9);
		expected.put("Vaccination - Administration Date", admin);
		ndc.setValued(100);
		ndc.setTotal(9);
		expected.put("Vaccination - Vaccine Type NDC", ndc);
		gender.setValued((3.0/4) * 100);
		gender.setExcluded((1.0/4) * 100);
		gender.setTotal(4);
		expected.put("Patient - Gender", gender);

		Map<String, ExtractPercent> actual = reader.getSummary().getExtract();

		for(String key : actual.keySet()) {
			if(expected.containsKey(key)) {
				assertEquals(key, expected.get(key), actual.get(key));
			} else {
				assertEquals(key,100, actual.get(key).getExcluded(), 0);
			}
		}
	}


	@Test
	public void metadataExpectations() throws IOException {
		Metadata metadata = reader.getMetadata();
		Properties properties = new Properties();
		properties.load(SqliteADFContentTestCase.class.getResourceAsStream("/application.properties"));
		String version = properties.getProperty("app.version");
		String build = properties.getProperty("app.date");
		String mqeVersion = properties.getProperty("mqe.version");
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");

		assertEquals(version, metadata.getVersion());
		assertEquals(build, metadata.getBuild());
		assertEquals(mqeVersion, metadata.getMqeVersion());
		assertTrue(metadata.getTotalAnalysisTime() > 0);
		assertEquals(0, metadata.getInactiveDetections().size());
		assertEquals(df.format(metadata.getAnalysisDate()), df.format(new Date()));
	}

	@Test
	public void configurationPayloadExpectation() {
		assertEquals(mock.getConfigurationPayload(), reader.getConfigurationPayload());
	}

	@Test
	public void dictionariesExpectations() {
		Dictionaries dictionaries = reader.getDictionaries();

		Map<String, Integer> provider = dictionaries.getValues(Field.PROVIDER);
		assertEquals(3, provider.size());
		assertTrue(
				provider.keySet().toString(),
				provider.keySet().containsAll(
						Stream.of("GRP_1", "GRP_2", "GRP_3")
								.map((g) -> DigestUtils.md5DigestAsHex(g.getBytes()))
								.collect(Collectors.toList())
				)
		);

		Map<String, Integer> detections = dictionaries.getValues(Field.DETECTION);
		assertEquals(3, detections.size());
		assertTrue(detections.keySet().toString(), detections.keySet().containsAll(Arrays.asList("MQE0683", "MQE0684", "MQE0559")));

		Map<String, Integer> tables = dictionaries.getValues(Field.TABLE);
		assertEquals(4, tables.size());
		assertTrue(tables.keySet().toString(), tables.keySet().containsAll(Arrays.asList("EVENT_NIP001", "CVX", "NDC", "GENDER_0001")));

		Map<String, Integer> codes = dictionaries.getValues(Field.CODE);
		assertEquals(9, codes.size());
		assertTrue(codes.keySet().toString(), codes.keySet().containsAll(Arrays.asList("00", "01", "08", "115", "58160-0820-01", "UNRECOGNIZED", "49281-0400-15", "F", "M")));

		Map<String, Integer> cvx = dictionaries.getValues(Field.VACCINE_CODE);
		assertEquals(2, cvx.size());
		assertTrue(cvx.keySet().toString(), cvx.keySet().containsAll(Arrays.asList("08", "115")));

		Map<String, Integer> events = dictionaries.getValues(Field.EVENT);
		assertEquals(2, events.size());
		assertTrue(events.keySet().toString(), events.keySet().containsAll(Arrays.asList("00", "01")));

		Map<String, Integer> years = dictionaries.getValues(Field.VACCINATION_YEAR);
		assertEquals(1, years.size());
		assertTrue(years.keySet().toString(), years.keySet().containsAll(Arrays.asList("2020")));

		Map<String, Integer> ageGroups = dictionaries.getValues(Field.AGE_GROUP);
		assertEquals(4, ageGroups.size());
		assertTrue(ageGroups.keySet().toString(), ageGroups.keySet().containsAll(Arrays.asList("0g", "1g", "2g", "3g")));

		Map<String, Integer> gender = dictionaries.getValues(Field.GENDER);
		assertEquals(3, gender.size());
		assertTrue(gender.keySet().toString(), gender.keySet().containsAll(Arrays.asList("", "F", "M")));

		Set<Field> fields = dictionaries.get().keySet();
		assertEquals(fields.size(), Field.values().length);
		assertTrue(fields.containsAll(Arrays.asList(Field.values())));

	}


	@Test
	public void checkVaccinationDetections() throws Exception {
		Dictionaries d = reader.getDictionaries();
		PreparedStatement search = reader.getConnection().prepareStatement("SELECT (count(*) = 1) as found FROM V_DETECTIONS WHERE PROVIDER_ID = ? AND AGE_GROUP = ? AND DETECTION_CODE = ? AND P = ? AND N = ?");
		PreparedStatement count = reader.getConnection().prepareStatement("SELECT count(*) as nb FROM V_DETECTIONS");
		List<List<Integer>> rows = Arrays.asList(
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_1".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.DETECTION, "MQE0559"), 2, 1),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_2".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.DETECTION, "MQE0559"), 2, 2),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_3".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.DETECTION, "MQE0559"), 1, 0),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_3".getBytes())),
						d.findId(Field.AGE_GROUP, "2g"),
						d.findId(Field.DETECTION, "MQE0559"), 1, 0)
		);
		sqliteAdfHelper.checkTableContent(search, count, rows);
	}

	@Test
	public void checkPatientDetections() throws Exception {
		Dictionaries d = reader.getDictionaries();
		PreparedStatement search = reader.getConnection().prepareStatement("SELECT (count(*) = 1) as found FROM P_DETECTIONS WHERE AGE_GROUP = ? AND DETECTION_CODE = ? AND P = ? AND N = ?");
		PreparedStatement count = reader.getConnection().prepareStatement("SELECT count(*) as nb FROM P_DETECTIONS");
		List<List<Integer>> rows = Arrays.asList(
				Arrays.asList(d.findId(Field.AGE_GROUP, "0g"), d.findId(Field.DETECTION, "MQE0683"), 1, 1),
				Arrays.asList(d.findId(Field.AGE_GROUP, "0g"), d.findId(Field.DETECTION, "MQE0684"), 0, 2),
				Arrays.asList(d.findId(Field.AGE_GROUP, "1g"), d.findId(Field.DETECTION, "MQE0683"), 0, 1),
				Arrays.asList(d.findId(Field.AGE_GROUP, "1g"), d.findId(Field.DETECTION, "MQE0684"), 1, 0),
				Arrays.asList(d.findId(Field.AGE_GROUP, "3g"), d.findId(Field.DETECTION, "MQE0683"), 1, 0),
				Arrays.asList(d.findId(Field.AGE_GROUP, "3g"), d.findId(Field.DETECTION, "MQE0684"), 1, 0)
		);
		sqliteAdfHelper.checkTableContent(search, count, rows);
	}

	@Test
	public void checkPatientVocabulary() throws Exception {
		Dictionaries d = reader.getDictionaries();
		PreparedStatement search = reader.getConnection().prepareStatement("SELECT (count(*) = 1) as found FROM P_VOCAB WHERE AGE_GROUP = ? AND VS = ? AND CODE = ? AND N = ?");
		PreparedStatement count = reader.getConnection().prepareStatement("SELECT count(*) as nb FROM P_VOCAB");
		List<List<Integer>> rows = Arrays.asList(
				Arrays.asList(d.findId(Field.AGE_GROUP, "0g"), d.findId(Field.TABLE, "GENDER_0001"), d.findId(Field.CODE, "F"), 2),
				Arrays.asList(d.findId(Field.AGE_GROUP, "3g"), d.findId(Field.TABLE, "GENDER_0001"), d.findId(Field.CODE, "M"), 1)
		);
		sqliteAdfHelper.checkTableContent(search, count, rows);
	}

	@Test
	public void checkVaccinationsVocabulary() throws Exception {
		Dictionaries d = reader.getDictionaries();
		PreparedStatement search = reader.getConnection().prepareStatement("SELECT (count(*) = 1) as found FROM V_VOCAB WHERE PROVIDER_ID = ? AND AGE_GROUP = ? AND VS = ? AND CODE = ? AND N = ?");
		PreparedStatement count = reader.getConnection().prepareStatement("SELECT count(*) as nb FROM V_VOCAB");
		List<List<Integer>> rows = Arrays.asList(
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_1".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.TABLE, "EVENT_NIP001"),
						d.findId(Field.CODE, "00"),
						3
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_1".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.TABLE, "CVX"),
						d.findId(Field.CODE, "08"),
						3
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_1".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.TABLE, "NDC"),
						d.findId(Field.CODE, "58160-0820-01"),
						2
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_1".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.TABLE, "NDC"),
						d.findId(Field.CODE, "UNRECOGNIZED"),
						1
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_2".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.TABLE, "EVENT_NIP001"),
						d.findId(Field.CODE, "00"),
						2
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_2".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.TABLE, "EVENT_NIP001"),
						d.findId(Field.CODE, "01"),
						2
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_2".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.TABLE, "CVX"),
						d.findId(Field.CODE, "08"),
						2
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_2".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.TABLE, "CVX"),
						d.findId(Field.CODE, "115"),
						2
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_2".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.TABLE, "NDC"),
						d.findId(Field.CODE, "UNRECOGNIZED"),
						2
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_2".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.TABLE, "NDC"),
						d.findId(Field.CODE, "49281-0400-15"),
						1
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_2".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.TABLE, "NDC"),
						d.findId(Field.CODE, "58160-0820-01"),
						1
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_3".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.TABLE, "EVENT_NIP001"),
						d.findId(Field.CODE, "00"),
						1
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_3".getBytes())),
						d.findId(Field.AGE_GROUP, "2g"),
						d.findId(Field.TABLE, "EVENT_NIP001"),
						d.findId(Field.CODE, "00"),
						1
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_3".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.TABLE, "CVX"),
						d.findId(Field.CODE, "08"),
						1
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_3".getBytes())),
						d.findId(Field.AGE_GROUP, "2g"),
						d.findId(Field.TABLE, "CVX"),
						d.findId(Field.CODE, "08"),
						1
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_3".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.TABLE, "NDC"),
						d.findId(Field.CODE, "58160-0820-01"),
						1
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_3".getBytes())),
						d.findId(Field.AGE_GROUP, "2g"),
						d.findId(Field.TABLE, "NDC"),
						d.findId(Field.CODE, "58160-0820-01"),
						1
				)
		);
		sqliteAdfHelper.checkTableContent(search, count, rows);
	}

	@Test
	public void checkVaccinations() throws Exception {
		Dictionaries d = reader.getDictionaries();
		PreparedStatement search = reader.getConnection().prepareStatement("SELECT (count(*) = 1) as found FROM V_EVENTS WHERE PROVIDER_ID = ? AND AGE_GROUP = ? AND YEAR = ? AND GENDER = ? AND SOURCE = ? AND CODE = ? AND N = ?");
		PreparedStatement count = reader.getConnection().prepareStatement("SELECT count(*) as nb FROM V_EVENTS");
		List<List<Integer>> rows = Arrays.asList(
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_1".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.VACCINATION_YEAR, "2020"),
						d.findId(Field.GENDER, "F"),
						d.findId(Field.EVENT, "00"),
						d.findId(Field.VACCINE_CODE, "08"),
						2
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_1".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.VACCINATION_YEAR, "2020"),
						d.findId(Field.GENDER, ""),
						d.findId(Field.EVENT, "00"),
						d.findId(Field.VACCINE_CODE, "08"),
						1
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_2".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.VACCINATION_YEAR, "2020"),
						d.findId(Field.GENDER, "F"),
						d.findId(Field.EVENT, "00"),
						d.findId(Field.VACCINE_CODE, "08"),
						2
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_2".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.VACCINATION_YEAR, "2020"),
						d.findId(Field.GENDER, "F"),
						d.findId(Field.EVENT, "01"),
						d.findId(Field.VACCINE_CODE, "115"),
						2
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_3".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.VACCINATION_YEAR, "2020"),
						d.findId(Field.GENDER, ""),
						d.findId(Field.EVENT, "00"),
						d.findId(Field.VACCINE_CODE, "08"),
						1
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_3".getBytes())),
						d.findId(Field.AGE_GROUP, "2g"),
						d.findId(Field.VACCINATION_YEAR, "2020"),
						d.findId(Field.GENDER, "M"),
						d.findId(Field.EVENT, "00"),
						d.findId(Field.VACCINE_CODE, "08"),
						1
				)
		);
		sqliteAdfHelper.checkTableContent(search, count, rows);
	}

	@Test
	public void checkPatientDetectionsByProvider() throws Exception {
		Dictionaries d = reader.getDictionaries();
		PreparedStatement search = reader.getConnection().prepareStatement("SELECT (count(*) = 1) as found FROM P_PROVIDER_DETECTIONS WHERE PROVIDER_ID = ? AND AGE_GROUP = ? AND DETECTION_CODE = ? AND P = ? AND N = ?");
		PreparedStatement count = reader.getConnection().prepareStatement("SELECT count(*) as nb FROM P_PROVIDER_DETECTIONS");
		List<List<Integer>> rows = Arrays.asList(
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_1".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.DETECTION, "MQE0683"),
						1,
						1
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_1".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.DETECTION, "MQE0684"),
						0,
						2
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_1".getBytes())),
						d.findId(Field.AGE_GROUP, "1g"),
						d.findId(Field.DETECTION, "MQE0683"),
						0,
						1
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_1".getBytes())),
						d.findId(Field.AGE_GROUP, "1g"),
						d.findId(Field.DETECTION, "MQE0684"),
						1,
						0
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_2".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.DETECTION, "MQE0683"),
						1,
						1
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_2".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.DETECTION, "MQE0684"),
						0,
						2
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_3".getBytes())),
						d.findId(Field.AGE_GROUP, "1g"),
						d.findId(Field.DETECTION, "MQE0683"),
						0,
						1
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_3".getBytes())),
						d.findId(Field.AGE_GROUP, "1g"),
						d.findId(Field.DETECTION, "MQE0684"),
						1,
						0
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_3".getBytes())),
						d.findId(Field.AGE_GROUP, "3g"),
						d.findId(Field.DETECTION, "MQE0683"),
						1,
						0
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_3".getBytes())),
						d.findId(Field.AGE_GROUP, "3g"),
						d.findId(Field.DETECTION, "MQE0684"),
						1,
						0
				)
		);
		sqliteAdfHelper.checkTableContent(search, count, rows);
	}

	@Test
	public void checkPatientVocabByProvider() throws Exception {
		Dictionaries d = reader.getDictionaries();
		PreparedStatement search = reader.getConnection().prepareStatement("SELECT (count(*) = 1) as found FROM P_PROVIDER_VOCAB WHERE PROVIDER_ID = ? AND AGE_GROUP = ? AND VS = ? AND CODE = ? AND N = ?");
		PreparedStatement count = reader.getConnection().prepareStatement("SELECT count(*) as nb FROM P_PROVIDER_VOCAB");
		List<List<Integer>> rows = Arrays.asList(
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_1".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.TABLE, "GENDER_0001"),
						d.findId(Field.CODE, "F"),
						2
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_2".getBytes())),
						d.findId(Field.AGE_GROUP, "0g"),
						d.findId(Field.TABLE, "GENDER_0001"),
						d.findId(Field.CODE, "F"),
						2
				),
				Arrays.asList(
						d.findId(Field.PROVIDER, DigestUtils.md5DigestAsHex("GRP_3".getBytes())),
						d.findId(Field.AGE_GROUP, "3g"),
						d.findId(Field.TABLE, "GENDER_0001"),
						d.findId(Field.CODE, "M"),
						1
				)
		);
		sqliteAdfHelper.checkTableContent(search, count, rows);
	}



	@AfterClass
	public static void close() throws Exception {
		if(reader != null) {
			reader.close();
		}
	}
}
