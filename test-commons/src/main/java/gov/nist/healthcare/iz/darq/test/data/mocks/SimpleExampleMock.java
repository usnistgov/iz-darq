package gov.nist.healthcare.iz.darq.test.data.mocks;

import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.test.data.DataExtractMock;
import gov.nist.healthcare.iz.darq.test.helper.AgeGroupHelper;
import gov.nist.healthcare.iz.darq.test.helper.ExtractBuilder;
import gov.nist.healthcare.iz.darq.test.helper.Record;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Set;

/*
	Mock Data Contains 4 patient Records

	As Of Date : 2020-12-14
	Detections :
		MQE0683 - First name is present,
		MQE0684 - Last name is present,
		MQE0559 - NDC code is unrecognized

	Record 1
	FIRST_NAME  LAST_NAME   AGE_GROUP   GENDER
	Y           Y           0           F
	VACCINATIONS
		REPORTING_GROUP     AGE_GROUP   EVENT   CVX     NDC             ELIGIBILITY     FUNDING
		GRP_1               0           ADMIN   8       58160-0820-01   Y               Y
		GRP_2               0           ADMIN   8       58160-0820-01   Y               N
		GRP_2               0           HIST    115     49281-0400-15   N               Y

	Record 2
	FIRST_NAME  LAST_NAME   AGE_GROUP   GENDER
	N           Y           0           F
	VACCINATIONS
		REPORTING_GROUP     AGE_GROUP   EVENT   CVX     NDC             ELIGIBILITY     FUNDING
		GRP_1               0           ADMIN   8       58160-0820-01   Y               Y
		GRP_2               0           ADMIN   8       UNRECOGNIZED    N               N
		GRP_2               0           HIST    115     UNRECOGNIZED    N               Y

	Record 3
	FIRST_NAME  LAST_NAME   AGE_GROUP   GENDER
	Y           N           1           [[EX]]
	VACCINATIONS
		REPORTING_GROUP     AGE_GROUP   EVENT   CVX     NDC             ELIGIBILITY     FUNDING
		GRP_1               0           ADMIN   8       UNRECOGNIZED    Y               Y
		GRP_3               0           ADMIN   8       58160-0820-01   Y               N

	Record 4
	FIRST_NAME  LAST_NAME   AGE_GROUP   GENDER
	N           N           3           M
	VACCINATIONS
		REPORTING_GROUP     AGE_GROUP   EVENT   CVX     NDC             ELIGIBILITY     FUNDING
		GRP_3               2           ADMIN   8       58160-0820-01   Y               N

 */

public class SimpleExampleMock implements DataExtractMock {
	static private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	static public final int PNF = 1;
	static public final int PNL = 3;
	static public final int VE = 19;
	static public final int VF = 20;

	AgeGroupHelper ageGroupHelper;

	private SimpleExampleMock() {
		ageGroupHelper = new AgeGroupHelper(LocalDate.parse("2020-12-14", DATE_FORMATTER), 3);
	}

	@Override
	public ConfigurationPayload getConfigurationPayload() {
		ConfigurationPayload configurationPayload = new ConfigurationPayload();
		configurationPayload.setAsOf("12/14/2020");
		configurationPayload.setDetections(Arrays.asList("MQE0683", "MQE0684", "MQE0559"));
		configurationPayload.setAgeGroups(ageGroupHelper.getAgeGroups());
		configurationPayload.setActivatePatientMatching(false);
		configurationPayload.setVaxCodeAbstraction(null);
		return configurationPayload;
	}

	@Override
	public Set<Record> getDataExtract() {
		return new ExtractBuilder(ageGroupHelper)
				.withRecord()
				// Record 1
				.withAgeGroup(0)
				.withValue(PNF, "[[VP]]").withValue(PNL, "[[VP]]").withVocabulary("GENDER_0001", "F")
				.withVaccination()
				.withAdminAtAgeGroup(0)
				.withValue(VE, "[[VP]]").withValue(VF, "[[VP]]").withReportingGroup("GRP_1")
				.withVocabulary("EVENT_NIP001", "00").withVocabulary("CVX", "8").withVocabulary("NDC", "58160-0820-01")
				.and()
				.withVaccination()
				.withAdminAtAgeGroup(0)
				.withValue(VE, "[[VP]]").withValue(VF, "[[NP]]").withReportingGroup("GRP_2")
				.withVocabulary("EVENT_NIP001", "00").withVocabulary("CVX", "8").withVocabulary("NDC", "58160-0820-01")
				.and()
				.withVaccination()
				.withAdminAtAgeGroup(0)
				.withValue(VE, "[[NP]]").withValue(VF, "[[VP]]").withReportingGroup("GRP_2")
				.withVocabulary("EVENT_NIP001", "01").withVocabulary("CVX", "115").withVocabulary("NDC", "49281-0400-15")
				.and()
				.and()
				.withRecord()
				// Record 2
				.withAgeGroup(0)
				.withValue(PNF, "[[NP]]").withValue(PNL, "[[VP]]").withVocabulary("GENDER_0001", "F")
				.withVaccination()
				.withAdminAtAgeGroup(0)
				.withValue(VE, "[[VP]]").withValue(VF, "[[VP]]").withReportingGroup("GRP_1")
				.withVocabulary("EVENT_NIP001", "00").withVocabulary("CVX", "8").withVocabulary("NDC", "58160-0820-01")
				.and()
				.withVaccination()
				.withAdminAtAgeGroup(0)
				.withValue(VE, "[[NP]]").withValue(VF, "[[NP]]").withReportingGroup("GRP_2")
				.withVocabulary("EVENT_NIP001", "00").withVocabulary("CVX", "8").withVocabulary("NDC", "UNRECOGNIZED")
				.and()
				.withVaccination()
				.withAdminAtAgeGroup(0)
				.withValue(VE, "[[NP]]").withValue(VF, "[[VP]]").withReportingGroup("GRP_2")
				.withVocabulary("EVENT_NIP001", "01").withVocabulary("CVX", "115").withVocabulary("NDC", "UNRECOGNIZED")
				.and()
				.and()
				.withRecord()
				// Record 3
				.withAgeGroup(1)
				.withValue(PNF, "[[VP]]").withValue(PNL, "[[NP]]")
				.withVaccination()
				.withAdminAtAgeGroup(0)
				.withValue(VE, "[[VP]]").withValue(VF, "[[VP]]").withReportingGroup("GRP_1")
				.withVocabulary("EVENT_NIP001", "00").withVocabulary("CVX", "8").withVocabulary("NDC", "UNRECOGNIZED")
				.and()
				.withVaccination()
				.withAdminAtAgeGroup(0)
				.withValue(VE, "[[VP]]").withValue(VF, "[[NP]]").withReportingGroup("GRP_3")
				.withVocabulary("EVENT_NIP001", "00").withVocabulary("CVX", "8").withVocabulary("NDC", "58160-0820-01")
				.and()
				.and()
				.withRecord()
				// Record 4
				.withAgeGroup(3)
				.withValue(PNF, "[[NP]]").withValue(PNL, "[[NP]]").withVocabulary("GENDER_0001", "M")
				.withVaccination()
				.withAdminAtAgeGroup(2)
				.withValue(VE, "[[VP]]").withValue(VF, "[[NP]]").withReportingGroup("GRP_3")
				.withVocabulary("EVENT_NIP001", "00").withVocabulary("CVX", "8").withVocabulary("NDC", "58160-0820-01")
				.and()
				.and()
				.get();
	}

	@Override
	public AgeGroupHelper getAgeGroupHelper() {
		return ageGroupHelper;
	}

	public static SimpleExampleMock get() {
		return new SimpleExampleMock();
	}
}
