import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.crypto.service.impl.JKSCryptoKey;
import gov.nist.healthcare.iz.darq.adf.module.sqlite.SqliteADFReader;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.DataTable;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.DataTableRow;
import gov.nist.healthcare.iz.darq.analyzer.model.template.*;
import gov.nist.healthcare.iz.darq.analyzer.service.sqlite.SqliteADFReportService;
import gov.nist.healthcare.iz.darq.digest.domain.AnalysisType;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.domain.Fraction;
import gov.nist.healthcare.iz.darq.test.helper.Constants;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.DigestUtils;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class SqliteADFAnalysisTestCase {

	@Autowired
	SqliteADFReportService reportService;
	static SqliteADFReader reader;
	static TemporaryFolder folder;

	@BeforeClass
	public static void setup() throws Exception {
		folder = new TemporaryFolder();
		folder.create();
		InputStream ADF = SqliteADFAnalysisTestCase.class.getResourceAsStream(Constants.SIMPLE_EXAMPLE_ADF);
		Path adfPath = Paths.get(folder.getRoot().getAbsolutePath(), "simple_example.adf");
		FileUtils.copyInputStreamToFile(ADF, adfPath.toFile());
		String root = folder.getRoot().getAbsolutePath();
		reader = new SqliteADFReader(adfPath.toAbsolutePath().toString(), root);
		CryptoKey cryptoKey = new JKSCryptoKey(Constants.class.getResourceAsStream(Constants.TEST_KEY), Constants.TEST_KEY_ALIAS, Constants.TEST_KEY_PASSWORD, Constants.TEST_KEY_PASSWORD);
		reader.read(cryptoKey);
	}

	@Test
	public void simpleQueryVaccinationDetection() throws Exception {
		SimpleViewQuery query = new SimpleViewQuery();
		GlobalThreshold threshold = new GlobalThreshold();
		threshold.setActive(false);
		query.setType(AnalysisType.VD);
		query.setFilterBy(new DataSingleSelector(Field.DETECTION, "MQE0559"));
		query.setNominator(Field.DETECTION);
		query.setThreshold(threshold);
		DataTable table = reportService.singleQuery(reader, query, null);
		assertDataTableContains(table, Arrays.asList(
				new DataTableRow(makeValuesMap(e(Field.DETECTION, "MQE0559")), new Fraction(3, 9), null, true)
		));
	}

	@Test
	public void advancedQueryVaccinationDetectionFilterByDetection() throws Exception {
		DataViewQuery query = new DataViewQuery();
		query.setSelectors(new HashSet<>(Arrays.asList(
				new DataSelector(Field.DETECTION, Arrays.asList(
						new ValueContainer("MQE0559")
				))
		)));
		query.setGroupBy(new HashSet<>(Arrays.asList(Field.PROVIDER)));
		query.setOccurrences(new HashSet<>(Arrays.asList(Field.DETECTION)));
		query.setType(AnalysisType.VD);
		DataTable table = reportService.singleQuery(reader, query, null);
		assertDataTableContains(table, Arrays.asList(
				new DataTableRow(
						makeValuesMap(
								e(Field.DETECTION, "MQE0559"),
								e(Field.PROVIDER, h("GRP_1"))
						),
						new Fraction(1, 3),
						null,
						true
				),
				new DataTableRow(
						makeValuesMap(
								e(Field.DETECTION, "MQE0559"),
								e(Field.PROVIDER, h("GRP_2"))
						),
						new Fraction(2, 4),
						null,
						true
				),
				new DataTableRow(
						makeValuesMap(
								e(Field.DETECTION, "MQE0559"),
								e(Field.PROVIDER, h("GRP_3"))
						),
						new Fraction(0, 2),
						null,
						true
				)
		));
	}

	@Test
	public void advancedQueryVaccinationDetectionFilterAgeGroup() throws Exception {
		DataViewQuery query = new DataViewQuery();
		query.setSelectors(new HashSet<>(Arrays.asList(
				new DataSelector(Field.AGE_GROUP, Arrays.asList(
						new ValueContainer("0g")
				))
		)));
		query.setOccurrences(new HashSet<>(Arrays.asList(Field.DETECTION)));
		query.setGroupBy(new HashSet<>(Arrays.asList(Field.PROVIDER)));
		query.setType(AnalysisType.VD);
		DataTable table = reportService.singleQuery(reader, query, null);
		assertDataTableContains(table, Arrays.asList(
				new DataTableRow(
						makeValuesMap(
								e(Field.DETECTION, "MQE0559"),
								e(Field.PROVIDER, h("GRP_1"))
						),
						new Fraction(1, 3),
						null,
						true
				),
				new DataTableRow(
						makeValuesMap(
								e(Field.DETECTION, "MQE0559"),
								e(Field.PROVIDER, h("GRP_2"))
						),
						new Fraction(2, 4),
						null,
						true
				),
				new DataTableRow(
						makeValuesMap(
								e(Field.DETECTION, "MQE0559"),
								e(Field.PROVIDER, h("GRP_3"))
						),
						new Fraction(0, 1),
						null,
						true
				)
		));
	}

	@Test
	public void advancedQueryVaccinationDetectionGroupByProviderAndAgeGroup() throws Exception {
		DataViewQuery query = new DataViewQuery();
		query.setSelectors(new HashSet<>());
		query.setOccurrences(new HashSet<>(Arrays.asList(Field.DETECTION)));
		query.setGroupBy(new HashSet<>(Arrays.asList(Field.PROVIDER, Field.AGE_GROUP)));
		query.setType(AnalysisType.VD);
		DataTable table = reportService.singleQuery(reader, query, null);
		assertDataTableContains(table, Arrays.asList(
				new DataTableRow(
						makeValuesMap(
								e(Field.DETECTION, "MQE0559"),
								e(Field.PROVIDER, h("GRP_1")),
								e(Field.AGE_GROUP, "0g")
						),
						new Fraction(1, 3),
						null,
						true
				),
				new DataTableRow(
						makeValuesMap(
								e(Field.DETECTION, "MQE0559"),
								e(Field.PROVIDER, h("GRP_2")),
								e(Field.AGE_GROUP,"0g")
						),
						new Fraction(2, 4),
						null,
						true
				),
				new DataTableRow(
						makeValuesMap(
								e(Field.DETECTION, "MQE0559"),
								e(Field.PROVIDER, h("GRP_3")),
								e(Field.AGE_GROUP, "0g")
						),
						new Fraction(0, 1),
						null,
						true
				),
				new DataTableRow(
						makeValuesMap(
								e(Field.DETECTION, "MQE0559"),
								e(Field.PROVIDER, h("GRP_3")),
								e(Field.AGE_GROUP, "2g")
						),
						new Fraction(0, 1),
						null,
						true
				)
		));
	}


	@Test
	public void simpleQueryPatientsDetection() throws Exception {
		SimpleViewQuery query = new SimpleViewQuery();
		GlobalThreshold threshold = new GlobalThreshold();
		threshold.setActive(false);
		query.setType(AnalysisType.PD);
		query.setFilterBy(new DataSingleSelector(Field.DETECTION, "MQE0683"));
		query.setNominator(Field.DETECTION);
		query.setThreshold(threshold);
		DataTable table = reportService.singleQuery(reader, query, null);
		assertDataTableContains(table, Arrays.asList(
				new DataTableRow(makeValuesMap(e(Field.DETECTION, "MQE0683")), new Fraction(2, 4), null, true)
		));
	}

	@Test
	public void advancedQueryPatientDetectionFilterByDetection() throws Exception {
		DataViewQuery query = new DataViewQuery();
		query.setSelectors(new HashSet<>(Arrays.asList(
				new DataSelector(Field.DETECTION, Arrays.asList(
						new ValueContainer("MQE0683")
				))
		)));
		query.setGroupBy(new HashSet<>(Arrays.asList(Field.AGE_GROUP)));
		query.setOccurrences(new HashSet<>(Arrays.asList(Field.DETECTION)));
		query.setType(AnalysisType.PD);
		DataTable table = reportService.singleQuery(reader, query, null);
		assertDataTableContains(table, Arrays.asList(
				new DataTableRow(
						makeValuesMap(
								e(Field.DETECTION, "MQE0683"),
								e(Field.AGE_GROUP, "0g")
						),
						new Fraction(1, 2),
						null,
						true
				),
				new DataTableRow(
						makeValuesMap(
								e(Field.DETECTION, "MQE0683"),
								e(Field.AGE_GROUP, "1g")
						),
						new Fraction(1, 1),
						null,
						true
				),
				new DataTableRow(
						makeValuesMap(
								e(Field.DETECTION, "MQE0683"),
								e(Field.AGE_GROUP, "3g")
						),
						new Fraction(0, 1),
						null,
						true
				)
		));
	}

	@Test
	public void advancedQueryPatientDetectionGroupByDetection() throws Exception {
		DataViewQuery query = new DataViewQuery();
		query.setSelectors(new HashSet<>());
		query.setGroupBy(new HashSet<>(Arrays.asList(Field.DETECTION)));
		query.setOccurrences(new HashSet<>());
		query.setType(AnalysisType.PD);
		DataTable table = reportService.singleQuery(reader, query, null);
		assertDataTableContains(table, Arrays.asList(
				new DataTableRow(
						makeValuesMap(
								e(Field.DETECTION, "MQE0683")
						),
						new Fraction(2, 4),
						null,
						true
				),
				new DataTableRow(
						makeValuesMap(
								e(Field.DETECTION, "MQE0684")
						),
						new Fraction(2, 4),
						null,
						true
				)
		));
	}

	@Test
	public void simpleQueryVaccinationVocab() throws Exception {
		SimpleViewQuery query = new SimpleViewQuery();
		GlobalThreshold threshold = new GlobalThreshold();
		threshold.setActive(false);
		query.setType(AnalysisType.VT);
		query.setFilterBy(new DataSingleSelector(Field.TABLE, "CVX"));
		query.setNominator(Field.CODE);
		query.setThreshold(threshold);
		DataTable table = reportService.singleQuery(reader, query, null);
		assertDataTableContains(table, Arrays.asList(
				new DataTableRow(
						makeValuesMap(
								e(Field.CODE, "08")
						),
						new Fraction(7, 9),
						null,
						true
				),
				new DataTableRow(
						makeValuesMap(
								e(Field.CODE, "115")
						),
						new Fraction(2, 9),
						null,
						true
				)
		));
	}

	@Test
	public void simpleQueryPatientVocab() throws Exception {
		SimpleViewQuery query = new SimpleViewQuery();
		GlobalThreshold threshold = new GlobalThreshold();
		threshold.setActive(false);
		query.setType(AnalysisType.PT);
		query.setFilterBy(new DataSingleSelector(Field.TABLE, "GENDER_0001"));
		query.setNominator(Field.CODE);
		query.setThreshold(threshold);
		DataTable table = reportService.singleQuery(reader, query, null);
		assertDataTableContains(table, Arrays.asList(
				new DataTableRow(
						makeValuesMap(
								e(Field.CODE, "M")
						),
						new Fraction(1, 3),
						null,
						true
				),
				new DataTableRow(
						makeValuesMap(
								e(Field.CODE, "F")
						),
						new Fraction(2, 3),
						null,
						true
				)
		));
	}

	@Test
	public void simpleQueryEvents() throws Exception {
		SimpleViewQuery query = new SimpleViewQuery();
		GlobalThreshold threshold = new GlobalThreshold();
		threshold.setActive(false);
		query.setType(AnalysisType.V);
		query.setNominator(Field.EVENT);
		query.setThreshold(threshold);
		DataTable table = reportService.singleQuery(reader, query, null);
		assertDataTableContains(table, Arrays.asList(
				new DataTableRow(
						makeValuesMap(
								e(Field.EVENT, "00")
						),
						new Fraction(7, 9),
						null,
						true
				),
				new DataTableRow(
						makeValuesMap(
								e(Field.EVENT, "01")
						),
						new Fraction(2, 9),
						null,
						true
				)
		));
	}

	@Test
	public void simpleQueryPatientDetectionsByProvider() throws Exception {
		SimpleViewQuery query = new SimpleViewQuery();
		GlobalThreshold threshold = new GlobalThreshold();
		SimpleQueryDenominator denominator = new SimpleQueryDenominator(Field.PROVIDER);
		denominator.setActive(true);
		threshold.setActive(false);
		query.setType(AnalysisType.PD_RG);
		query.setDenominator(denominator);
		query.setNominator(Field.DETECTION);
		query.setFilterBy(new DataSingleSelector(Field.DETECTION, "MQE0683"));
		query.setThreshold(threshold);
		DataTable table = reportService.singleQuery(reader, query, null);
		assertDataTableContains(table, Arrays.asList(
				new DataTableRow(
						makeValuesMap(
								e(Field.DETECTION, "MQE0683"),
								e(Field.PROVIDER, h("GRP_1"))
						),
						new Fraction(2, 3),
						null,
						true
				),
				new DataTableRow(
						makeValuesMap(
								e(Field.DETECTION, "MQE0683"),
								e(Field.PROVIDER, h("GRP_2"))
						),
						new Fraction(1, 2),
						null,
						true
				),
				new DataTableRow(
						makeValuesMap(
								e(Field.DETECTION, "MQE0683"),
								e(Field.PROVIDER, h("GRP_3"))
						),
						new Fraction(1, 2),
						null,
						true
				)
		));
	}

	@Test
	public void simpleQueryPatientVocabularyByProvider() throws Exception {
		SimpleViewQuery query = new SimpleViewQuery();
		GlobalThreshold threshold = new GlobalThreshold();
		SimpleQueryDenominator denominator = new SimpleQueryDenominator(Field.PROVIDER);
		denominator.setActive(true);
		threshold.setActive(false);
		query.setType(AnalysisType.PT_RG);
		query.setDenominator(denominator);
		query.setNominator(Field.CODE);
		query.setFilterBy(new DataSingleSelector(Field.TABLE, "GENDER_0001"));
		query.setThreshold(threshold);
		DataTable table = reportService.singleQuery(reader, query, null);
		assertDataTableContains(table, Arrays.asList(
				new DataTableRow(
						makeValuesMap(
								e(Field.CODE, "F"),
								e(Field.PROVIDER, h("GRP_1"))
						),
						new Fraction(2, 2),
						null,
						true
				),
				new DataTableRow(
						makeValuesMap(
								e(Field.CODE, "F"),
								e(Field.PROVIDER, h("GRP_2"))
						),
						new Fraction(2, 2),
						null,
						true
				),
				new DataTableRow(
						makeValuesMap(
								e(Field.CODE, "M"),
								e(Field.PROVIDER, h("GRP_3"))
						),
						new Fraction(1, 1),
						null,
						true
				)
		));
	}


	public void assertDataTableContains(DataTable table, List<DataTableRow> rows) {
		assertEquals("Result table contains more rows than expected", rows.size(), table.getValues().size());
		for(DataTableRow row : rows) {
			List<DataTableRow> rowMatch = table.getValues().stream().filter((r) -> r.getValues().equals(row.getValues())).collect(Collectors.toList());
			assertEquals("Row " + row.getValues() + " has more/less matches than expected", 1, rowMatch.size());
			DataTableRow match = rowMatch.get(0);
			assertRowMatches(row, match);
		}
	}

	public String h(String value) {
		return DigestUtils.md5DigestAsHex(value.getBytes());
	}


	@SafeVarargs
	public final Map<Field, String> makeValuesMap(Map.Entry<Field, String>... entries) {
		Map<Field, String> result = new HashMap<>();
		for(Map.Entry<Field, String> entry : entries) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public AbstractMap.SimpleEntry<Field, String> e(Field field, String value) {
		return new AbstractMap.SimpleEntry<>(field, value);
	}

	public void assertRowMatches(DataTableRow expected, DataTableRow actual) {
		boolean values = Objects.equals(expected.getValues(), actual.getValues());
		boolean fraction = Objects.equals(expected.getResult(), actual.getResult());
		boolean threshold = Objects.equals(expected.getThreshold(), actual.getThreshold());
		boolean pass = Objects.equals(expected.isPass(), actual.isPass());
		assertTrue("Values should be equal", values);
		assertTrue("Fraction should be equal", fraction);
		assertTrue("Threshold should be equal", threshold);
		assertTrue("Threshold activation should be equal", pass);
	}

	@AfterClass
	public static void close() throws Exception {
		if(reader != null) {
			reader.close();
		}
	}
}
