package gov.nist.healthcare.iz.darq.digest.mock;

import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.test.data.DataExtractMock;
import gov.nist.healthcare.iz.darq.test.helper.AgeGroupHelper;
import gov.nist.healthcare.iz.darq.test.helper.ExtractBuilder;
import gov.nist.healthcare.iz.darq.test.helper.Record;
import org.immregistries.mqe.validator.detection.Detection;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*

	RECORD 1 -- invalidDosesPatientId
		8 DTaP doses crammed into the first weeks of life, violating both the minimum age and the
		minimum interval, so the forecaster evaluates 5+ of them as invalid
		-> VaccineEvaluationHasInvalidDoses5orMore -> column 1 "X"
	RECORD 2 -- fluDosesPatientId
		2 flu doses inside the 2021 season (Sep 2021 - Mar 2022), patient aged 16 at both
		-> PatientFluSeasonDoseCountIs2OrMore -> column 2 "X"
	RECORD 3 -- covidDosesPatientId
		4 COVID doses administered in calendar year 2021, spaced apart
		-> PatientCovid2021DoseCountIs4OrMore -> column 3 "X"
	RECORD 4 -- cleanPatientId
		a single COVID dose (below the 4 threshold) and a single flu dose (below the 2 threshold)
		-> no detection -> absent from the report

 */

public class MismergedPatientCandidatesReportMock implements DataExtractMock {
	static private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	// Patient record field indices
	static public final int PNF = 1;
	static public final int PNM = 2;
	static public final int PNL = 3;

	// Vaccination record field indices (see VaccineRecord)
	static public final int CVX = 7;
	static public final int ADMIN_DATE = 9;
	static public final int LOT_NUMBER = 11;
	static public final int EVENT = 12;

	// CVX codes the rules key on
	static public final String CVX_DTAP = "20";
	static public final String CVX_COVID = "208";   // PatientCovid2021DoseCount COVID_CVX_CODES
	static public final String CVX_FLU = "150";     // PatientFluSeasonDoseCount FLU_CVX_CODES

	/*
	 * This report is keyed on Patient ID, so the IDs have to be stable for the assertions to
	 * reference. RecordBuilder generates a random ID by default - .withID(..) pins it.
	 */
	public final String invalidDosesPatientId = "PAT-INVALID-DOSES";
	public final String fluDosesPatientId = "PAT-FLU-DOSES";
	public final String covidDosesPatientId = "PAT-COVID-DOSES";
	public final String cleanPatientId = "PAT-CLEAN";

	public final AgeGroupHelper ageGroupHelper;
	public final ConfigurationPayload configurationPayload;

	protected MismergedPatientCandidatesReportMock() {
		/*
		 * Evaluated as of mid-2022 so that the 2021 COVID doses and the Sep 2021 - Mar 2022 flu
		 * season are both in the past. The age groups themselves are irrelevant to this report -
		 * patients older than the last bracket fall into AgeGroupCalculator's overflow group
		 * rather than being dropped.
		 */
		ageGroupHelper = new AgeGroupHelper(LocalDate.parse("2022-06-15", DATE_FORMATTER), 3);
		configurationPayload = new ConfigurationPayload();
		configurationPayload.setAsOf("06/15/2022");
		// Must cover MismergedPatientCandidatesReportService.DETECTIONS, otherwise the report is
		// skipped entirely (see LocalReportService.dependenciesAreMet).
		configurationPayload.setDetections(Stream.of(
				Detection.VaccineEvaluationHasInvalidDoses5orMore,
				Detection.PatientFluSeasonDoseCountIs2OrMore,
				Detection.PatientCovid2021DoseCountIs4OrMore,
				Detection.PatientCovid2021DoseCountIs5OrMore,
				Detection.PatientCovid2021DoseCountIs6OrMore
		).map(Detection::getMqeMqeCode).collect(Collectors.toList()));
		configurationPayload.setAgeGroups(ageGroupHelper.getAgeGroups());
		configurationPayload.setActivatePatientMatching(false);
		configurationPayload.setVaxCodeAbstraction(null);
	}

	@Override
	public ConfigurationPayload getConfigurationPayload() {
		return configurationPayload;
	}

	@Override
	public List<Record> getDataExtract() {
		ExtractBuilder extractBuilder = new ExtractBuilder(ageGroupHelper)

				// --- Column 1: 5 or more invalid doses ---------------------------------------
				// Every dose is given far below the minimum age for DTaP and days apart, so the
				// forecaster evaluates them as invalid.
				.withRecord()
				.withID(invalidDosesPatientId)
				.withDOB("2021-01-01")
				.withValue(PNF, "Alan").withValue(PNM, "Mathison").withValue(PNL, "Turing")
				.withVocabulary("GENDER_0001", "M")
					.withVaccination().withReportingGroup("ANY")
						.withValue(CVX, CVX_DTAP).withValue(ADMIN_DATE, "2021-01-02")
						.withValue(LOT_NUMBER, "LOT1").withValue(EVENT, "00")
					.and()
					.withVaccination().withReportingGroup("ANY")
						.withValue(CVX, CVX_DTAP).withValue(ADMIN_DATE, "2021-01-05")
						.withValue(LOT_NUMBER, "LOT2").withValue(EVENT, "00")
					.and()
					.withVaccination().withReportingGroup("ANY")
						.withValue(CVX, CVX_DTAP).withValue(ADMIN_DATE, "2021-01-08")
						.withValue(LOT_NUMBER, "LOT3").withValue(EVENT, "00")
					.and()
					.withVaccination().withReportingGroup("ANY")
						.withValue(CVX, CVX_DTAP).withValue(ADMIN_DATE, "2021-01-11")
						.withValue(LOT_NUMBER, "LOT4").withValue(EVENT, "00")
					.and()
					.withVaccination().withReportingGroup("ANY")
						.withValue(CVX, CVX_DTAP).withValue(ADMIN_DATE, "2021-01-14")
						.withValue(LOT_NUMBER, "LOT5").withValue(EVENT, "00")
					.and()
					.withVaccination().withReportingGroup("ANY")
						.withValue(CVX, CVX_DTAP).withValue(ADMIN_DATE, "2021-01-17")
						.withValue(LOT_NUMBER, "LOT6").withValue(EVENT, "00")
					.and()
					.withVaccination().withReportingGroup("ANY")
						.withValue(CVX, CVX_DTAP).withValue(ADMIN_DATE, "2021-01-20")
						.withValue(LOT_NUMBER, "LOT7").withValue(EVENT, "00")
					.and()
					.withVaccination().withReportingGroup("ANY")
						.withValue(CVX, CVX_DTAP).withValue(ADMIN_DATE, "2021-01-23")
						.withValue(LOT_NUMBER, "LOT8").withValue(EVENT, "00")
					.and()
				.and()

				// --- Column 2: 2 or more flu doses in one season -----------------------------
				// Season "2021" runs Sep 2021 - Mar 2022. Born 2005, so 16 at both doses, above
				// the rule's minimum age of 10.
				.withRecord()
				.withID(fluDosesPatientId)
				.withDOB("2005-01-10")
				.withValue(PNF, "Grace").withValue(PNM, "Brewster").withValue(PNL, "Hopper")
				.withVocabulary("GENDER_0001", "F")
					.withVaccination().withReportingGroup("ANY")
						.withValue(CVX, CVX_FLU).withValue(ADMIN_DATE, "2021-10-05")
						.withValue(LOT_NUMBER, "FLU1").withValue(EVENT, "00")
					.and()
					.withVaccination().withReportingGroup("ANY")
						.withValue(CVX, CVX_FLU).withValue(ADMIN_DATE, "2021-12-08")
						.withValue(LOT_NUMBER, "FLU2").withValue(EVENT, "00")
					.and()
				.and()

				// --- Column 3: 4 or more COVID doses in 2021 ---------------------------------
				// Exactly 4, so only the 4-or-more threshold fires (5 and 6 stay silent).
				.withRecord()
				.withID(covidDosesPatientId)
				.withDOB("1985-06-12")
				.withValue(PNF, "Katherine").withValue(PNM, "Coleman").withValue(PNL, "Johnson")
				.withVocabulary("GENDER_0001", "F")
					.withVaccination().withReportingGroup("ANY")
						.withValue(CVX, CVX_COVID).withValue(ADMIN_DATE, "2021-01-15")
						.withValue(LOT_NUMBER, "COV1").withValue(EVENT, "00")
					.and()
					.withVaccination().withReportingGroup("ANY")
						.withValue(CVX, CVX_COVID).withValue(ADMIN_DATE, "2021-04-20")
						.withValue(LOT_NUMBER, "COV2").withValue(EVENT, "00")
					.and()
					.withVaccination().withReportingGroup("ANY")
						.withValue(CVX, CVX_COVID).withValue(ADMIN_DATE, "2021-08-10")
						.withValue(LOT_NUMBER, "COV3").withValue(EVENT, "00")
					.and()
					.withVaccination().withReportingGroup("ANY")
						.withValue(CVX, CVX_COVID).withValue(ADMIN_DATE, "2021-11-05")
						.withValue(LOT_NUMBER, "COV4").withValue(EVENT, "00")
					.and()
				.and()

				// --- Control: below every threshold, must not appear in the report -----------
				.withRecord()
				.withID(cleanPatientId)
				.withDOB("1990-03-22")
				.withValue(PNF, "Ada").withValue(PNM, "Byron").withValue(PNL, "Lovelace")
				.withVocabulary("GENDER_0001", "F")
					.withVaccination().withReportingGroup("ANY")
						.withValue(CVX, CVX_COVID).withValue(ADMIN_DATE, "2021-03-02")
						.withValue(LOT_NUMBER, "COV1").withValue(EVENT, "00")
					.and()
					.withVaccination().withReportingGroup("ANY")
						.withValue(CVX, CVX_FLU).withValue(ADMIN_DATE, "2021-10-12")
						.withValue(LOT_NUMBER, "FLU1").withValue(EVENT, "00")
					.and()
				.and();
		return extractBuilder.get();
	}

	@Override
	public AgeGroupHelper getAgeGroupHelper() {
		return ageGroupHelper;
	}

	public static MismergedPatientCandidatesReportMock get() {
		return new MismergedPatientCandidatesReportMock();
	}
}