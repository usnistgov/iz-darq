package gov.nist.healthcare.iz.darq.test.data.mocks;

import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.test.data.DataExtractMock;
import gov.nist.healthcare.iz.darq.test.helper.AgeGroupHelper;
import gov.nist.healthcare.iz.darq.test.helper.ExtractBuilder;
import gov.nist.healthcare.iz.darq.test.helper.Record;
import org.apache.commons.io.IOUtils;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/*
	Mock Data Contains 4 patient Records

	As Of Date : 2020-12-14
	Detections :
		PM001 - Record is possible duplicate

	Record 1
	FIRST_NAME  MIDDLE_NAME LAST_NAME   AGE_GROUP   GENDER
	Alan        Mathison    Turing      0           M

	Record 2
	FIRST_NAME  MIDDLE_NAME LAST_NAME   AGE_GROUP   GENDER
	Alan        Mathison    Turing      0           M

	Record 3
	FIRST_NAME  MIDDLE_NAME LAST_NAME   AGE_GROUP   GENDER
	Ada         Augusta     Lovelace    1           F

	Record 4
	FIRST_NAME  MIDDLE_NAME LAST_NAME   AGE_GROUP   GENDER
	Ada         Augusta     Lovelace    1           F

	Record 5
	FIRST_NAME  MIDDLE_NAME LAST_NAME   AGE_GROUP   GENDER
	Testy       Teston     McTester    0           M
 */

public class DuplicatePatientExampleMock implements DataExtractMock {
	static private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	static public final int PNF = 1;
	static public final int PNM = 2;
	static public final int PNL = 3;

	AgeGroupHelper ageGroupHelper;

	protected DuplicatePatientExampleMock() {
		ageGroupHelper = new AgeGroupHelper(LocalDate.parse("2020-12-14", DATE_FORMATTER), 3);
	}

	@Override
	public ConfigurationPayload getConfigurationPayload() {
		ConfigurationPayload configurationPayload = new ConfigurationPayload();
		configurationPayload.setAsOf("12/14/2020");
		configurationPayload.setDetections(Collections.singletonList("PM001"));
		configurationPayload.setAgeGroups(ageGroupHelper.getAgeGroups());
		String configuration = String.join(
				"\n",
				IOUtils.readLines(
						DuplicatePatientExampleMock.class.getResourceAsStream("/test_configuration/Configuration.yml"),
						Charset.defaultCharset()
				)
		);
		configurationPayload.setActivatePatientMatching(true);
		configurationPayload.setMismoPatientMatchingConfiguration(configuration);
		configurationPayload.setVaxCodeAbstraction(null);
		return configurationPayload;
	}

	@Override
	public List<Record> getDataExtract() {
		return new ExtractBuilder(ageGroupHelper)
				.withRecord()
				// Record 1
				.withAgeGroup(0)
					.withValue(PNF, "Alan")
					.withValue(PNM, "Mathison")
					.withValue(PNL, "Turing")
					.withVocabulary("GENDER_0001", "M")
				.and()
				.withRecord()
				// Record 2
				.withAgeGroup(0)
					.withValue(PNF, "Alan")
					.withValue(PNM, "Mathison")
					.withValue(PNL, "Turing")
					.withVocabulary("GENDER_0001", "M")
				.and()
				.withRecord()
				// Record 3
				.withAgeGroup(1)
					.withValue(PNF, "Ada")
					.withValue(PNM, "Augusta")
					.withValue(PNL, "Lovelace")
					.withVocabulary("GENDER_0001", "F")
				.and()
				.withRecord()
				// Record 4
				.withAgeGroup(1)
					.withValue(PNF, "Ada")
					.withValue(PNM, "Augusta")
					.withValue(PNL, "Lovelace")
					.withVocabulary("GENDER_0001", "F")
				.and()
				.withRecord()
				// Record 5
				.withAgeGroup(0)
					.withValue(PNF, "Testy")
					.withValue(PNM, "Teston")
					.withValue(PNL, "McTester")
					.withVocabulary("GENDER_0001", "M")
				.and()
				.get();
	}

	@Override
	public AgeGroupHelper getAgeGroupHelper() {
		return ageGroupHelper;
	}

	public static DuplicatePatientExampleMock get() {
		return new DuplicatePatientExampleMock();
	}
}
