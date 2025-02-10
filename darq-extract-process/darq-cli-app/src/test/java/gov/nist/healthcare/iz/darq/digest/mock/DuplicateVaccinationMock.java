package gov.nist.healthcare.iz.darq.digest.mock;

import gov.nist.healthcare.iz.darq.detections.codes.VaccinationDuplicateDetection;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.test.data.DataExtractMock;
import gov.nist.healthcare.iz.darq.test.helper.AgeGroupHelper;
import gov.nist.healthcare.iz.darq.test.helper.ExtractBuilder;
import gov.nist.healthcare.iz.darq.test.helper.Record;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

public class DuplicateVaccinationMock implements DataExtractMock {
	static private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	static public final int CVX = 7;
	static public final int PNF = 1;
	static public final int PNM = 2;
	static public final int PNL = 3;
	static public final int LOT_NUMBER = 11;
	static public final int EVENT = 12;


	AgeGroupHelper ageGroupHelper;

	protected DuplicateVaccinationMock() {
		ageGroupHelper = new AgeGroupHelper(LocalDate.parse("2020-12-14", DATE_FORMATTER), 3);
	}

	@Override
	public ConfigurationPayload getConfigurationPayload() {
		ConfigurationPayload configurationPayload = new ConfigurationPayload();
		configurationPayload.setAsOf("12/14/2020");
		configurationPayload.setDetections(Stream.of(VaccinationDuplicateDetection.VD0001.name()).collect(Collectors.toList()));
		configurationPayload.setAgeGroups(ageGroupHelper.getAgeGroups());
		configurationPayload.setActivatePatientMatching(false);
		configurationPayload.setVaxCodeAbstraction(null);
		return configurationPayload;
	}

	@Override
	public List<Record> getDataExtract() {
		/*
			RECORD 1
				V1
				v2 -> Duplicate of V1
				v3
			RECORD 2
				V1
				V2
				V3 -> duplicate of v2
			RECORD 3
				V1
				V2
		 */

		ExtractBuilder extractBuilder = new ExtractBuilder(ageGroupHelper)
				.withRecord()
				// Record 1
				.withAgeGroup(0)
				.withValue(PNF, "Alan")
				.withValue(PNM, "Mathison")
				.withValue(PNL, "Turing")
				.withVocabulary("GENDER_0001", "M")
					.withVaccination()
						.withAdminAtAgeGroup(0)
						.withReportingGroup("ANY")
						.withValue(CVX, "08")
						.withValue(LOT_NUMBER, "BBB")
						.withValue(EVENT, "00")
					.and()
					// Duplicate
					.withVaccination()
						.withAdminAtAgeGroup(0)
						.withReportingGroup("ANY")
						.withValue(CVX, "08")
						.withValue(LOT_NUMBER, "BBB")
						.withValue(EVENT, "00")
					.and()
					.withVaccination()
						.withAdminAtAgeGroup(0)
						.withReportingGroup("ANY")
						.withValue(CVX, "145")
						.withValue(LOT_NUMBER, "AAA")
						.withValue(EVENT, "00")
					.and()
				.and()
				.withRecord()
				// Record 2
				.withAgeGroup(0)
				.withValue(PNF, "Ada")
				.withValue(PNM, "Augusta")
				.withValue(PNL, "Lovelace")
				.withVocabulary("GENDER_0001", "F")
				.withVaccination()
					.withAdminAtAgeGroup(0)
					.withReportingGroup("ANY")
					.withValue(CVX, "08")
					.withValue(LOT_NUMBER, "BBB")
					.withValue(EVENT, "00")
				.and()
				.withVaccination()
					.withAdminAtAgeGroup(0)
					.withReportingGroup("ANY")
					.withValue(CVX, "145")
					.withValue(LOT_NUMBER, "AAA")
					.withValue(EVENT, "00")
				.and()
				.withVaccination()
					.withAdminAtAgeGroup(0)
					.withReportingGroup("ANY")
					.withValue(CVX, "145")
					.withValue(LOT_NUMBER, "AAA")
					.withValue(EVENT, "00")
				.and()
				.withVaccination()
					.withAdminAtAgeGroup(0)
					.withReportingGroup("ANY")
					.withValue(CVX, "145")
					.withValue(LOT_NUMBER, "AAA")
					.withValue(EVENT, "00")
				.and()
				.and()
				.withRecord()
				// Record 3
				.withAgeGroup(0)
				.withValue(PNF, "Albert")
				.withValue(PNL, "Einstein")
				.withVocabulary("GENDER_0001", "M")
				.withVaccination()
					.withAdminAtAgeGroup(0)
					.withReportingGroup("ANY")
					.withValue(CVX, "08")
					.withValue(LOT_NUMBER, "AAA")
					.withValue(EVENT, "01")
				.and()
				.withVaccination()
					.withAdminAtAgeGroup(0)
					.withReportingGroup("ANY")
					.withValue(CVX, "145")
					.withValue(LOT_NUMBER, "BBB")
					.withValue(EVENT, "01")
				.and().and();
		return extractBuilder.get();
	}

	@Override
	public AgeGroupHelper getAgeGroupHelper() {
		return ageGroupHelper;
	}



	public static DuplicateVaccinationMock get() {
		return new DuplicateVaccinationMock();
	}
}
