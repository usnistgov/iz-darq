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
	TODO fill in the extract. Sketch of what each record needs to trigger:

	RECORD invalidDosesPatientId
		enough invalid-dose vaccinations to raise VaccineEvaluationHasInvalidDoses5orMore
		-> column 1 marked "X"
	RECORD fluDosesPatientId
		2+ flu doses in one season, raising PatientFluSeasonDoseCountIs2OrMore
		-> column 2 marked "X"
	RECORD covidDosesPatientId
		4+ COVID doses in 2021, raising PatientCovid2021DoseCountIs4OrMore (and 5/6 as the
		count climbs) -> column 3 marked "X"
	RECORD cleanPatientId
		an ordinary history raising none of the above -> not in the report at all
 */

public class MismergedPatientCandidatesReportMock implements DataExtractMock {
	static private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	// Patient record field indices
	static public final int PNF = 1;
	static public final int PNM = 2;
	static public final int PNL = 3;

	// Vaccination record field indices
	static public final int CVX = 7;
	static public final int LOT_NUMBER = 11;
	static public final int EVENT = 12;

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
		ageGroupHelper = new AgeGroupHelper(LocalDate.parse("2020-12-14", DATE_FORMATTER), 3);
		configurationPayload = new ConfigurationPayload();
		configurationPayload.setAsOf("12/14/2020");
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
				// --- Column 1: more than 5 invalid doses -------------------------------------
				.withRecord()
				.withID(invalidDosesPatientId)
				.withAgeGroup(0)
				.withValue(PNF, "Alan").withValue(PNM, "Mathison").withValue(PNL, "Turing")
				// TODO add the vaccinations that raise VaccineEvaluationHasInvalidDoses5orMore:
				//   .withVaccination()
				//       .withAdminAtAgeGroup(0)
				//       .withReportingGroup("ANY")
				//       .withValue(CVX, "..")
				//       .withValue(LOT_NUMBER, "..")
				//       .withValue(EVENT, "00")
				//   .and()
				.and()

				// --- Column 2: too many flu doses --------------------------------------------
				.withRecord()
				.withID(fluDosesPatientId)
				.withAgeGroup(0)
				.withValue(PNF, "Alan").withValue(PNM, "Mathison").withValue(PNL, "Turing")
				// TODO add 2+ flu doses in the same season
				.and()

				// --- Column 3: too many COVID doses ------------------------------------------
				.withRecord()
				.withID(covidDosesPatientId)
				.withAgeGroup(0)
				.withValue(PNF, "Alan").withValue(PNM, "Mathison").withValue(PNL, "Turing")
				// TODO add 4+ COVID doses administered in 2021
				.and()

				// --- Control: no mismerge signal, must not appear in the report --------------
				.withRecord()
				.withID(cleanPatientId)
				.withAgeGroup(0)
				.withValue(PNF, "Alan").withValue(PNM, "Mathison").withValue(PNL, "Turing")
				// TODO add an ordinary vaccination history
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