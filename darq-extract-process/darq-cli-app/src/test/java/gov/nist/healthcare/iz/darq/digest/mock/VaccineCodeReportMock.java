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
	CVX codes in the spellings an extract realistically contains. CodeParseStatsUtil.processVaccineCodes
	counts every non-blank CVX per record, reading the value AFTER the parser has applied
	CVXFieldTransformation - which pads a bare digit ("8" -> "08") and trims a 3-digit form
	("008" -> "08"). All three spellings therefore aggregate into the single code "08".

	RECORD 1
		VX 1 => 08   Hep B ped
		VX 2 => 20   DTaP
	RECORD 2
		VX 1 => 08   same code twice inside one record
		VX 2 => 08
	RECORD 3
		VX 1 => 8    padded by the parser to 08
		VX 2 => 008  trimmed by the parser to 08
		VX 3 => 140  Influenza
	RECORD 4
		VX 1 => 03   MMR
		VX 2 => (not set) -> blank, not counted
 */

public class VaccineCodeReportMock implements DataExtractMock {
	static private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	// Patient record field indices
	static public final int PNF = 1;
	static public final int PNM = 2;
	static public final int PNL = 3;

	// Vaccination record field indices (see VaccineRecord)
	static public final int CVX = 7;
	static public final int MVX = 10;
	static public final int LOT_NUMBER = 11;
	static public final int EVENT = 12;

	// Real CVX codes, in the spellings the extract carries.
	public final String cvxHepBPed = "08";        // canonical form
	public final String cvxHepBPedBare = "8";     // parser pads to "08"
	public final String cvxHepBPedPadded = "008"; // parser trims to "08"
	public final String cvxDtap = "20";
	public final String cvxInfluenza = "140";
	public final String cvxMmr = "03";

	// The single code that "08", "8" and "008" all normalise to.
	public final String cvxHepBPedNormalised = "08";

	public final String patientTwoDistinctCodes = "PAT-CVX-DISTINCT";
	public final String patientRepeatedCode = "PAT-CVX-REPEATED";
	public final String patientNormalisedCodes = "PAT-CVX-NORMALISED";
	public final String patientBlankCode = "PAT-CVX-BLANK";

	public final AgeGroupHelper ageGroupHelper;
	public final ConfigurationPayload configurationPayload;

	protected VaccineCodeReportMock() {
		ageGroupHelper = new AgeGroupHelper(LocalDate.parse("2020-12-14", DATE_FORMATTER), 3);
		configurationPayload = new ConfigurationPayload();
		configurationPayload.setAsOf("12/14/2020");
		/*
		 * VaccineCodeReportService declares no required detections (super(FILENAME)), so the
		 * report is produced regardless of what is configured here - the counting happens in
		 * SimpleDigestRunner's preprocess, before any detection runs. A realistic admin-code
		 * detection set is configured anyway; VaccineCodeReportNoDetectionsTestCase clears it to
		 * show the report is produced either way.
		 */
		configurationPayload.setDetections(Stream.of(
				Detection.VaccinationAdminCodeIsMissing,
				Detection.VaccinationAdminCodeIsPresent,
				Detection.VaccinationAdminCodeIsInvalid,
				Detection.VaccinationAdminCodeIsUnrecognized,
				Detection.VaccinationAdminCodeIsDeprecated
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

				// --- Two distinct codes, once each ------------------------------------------
				.withRecord()
				.withID(patientTwoDistinctCodes)
				.withAgeGroup(0)
				.withValue(PNF, "Alan").withValue(PNM, "Mathison").withValue(PNL, "Turing")
					.withVaccination().withAdminAtAgeGroup(0).withReportingGroup("ANY")
						.withValue(CVX, cvxHepBPed).withValue(MVX, "MSD")
						.withValue(LOT_NUMBER, "A12345").withValue(EVENT, "00")
					.and()
					.withVaccination().withAdminAtAgeGroup(0).withReportingGroup("ANY")
						.withValue(CVX, cvxDtap).withValue(MVX, "PMC")
						.withValue(LOT_NUMBER, "A12346").withValue(EVENT, "00")
					.and()
				.and()

				// --- Same code twice inside a single record ---------------------------------
				.withRecord()
				.withID(patientRepeatedCode)
				.withAgeGroup(0)
				.withValue(PNF, "Grace").withValue(PNM, "Brewster").withValue(PNL, "Hopper")
					.withVaccination().withAdminAtAgeGroup(0).withReportingGroup("ANY")
						.withValue(CVX, cvxHepBPed).withValue(MVX, "MSD")
						.withValue(LOT_NUMBER, "B10001").withValue(EVENT, "00")
					.and()
					.withVaccination().withAdminAtAgeGroup(0).withReportingGroup("ANY")
						.withValue(CVX, cvxHepBPed).withValue(MVX, "MSD")
						.withValue(LOT_NUMBER, "B10002").withValue(EVENT, "00")
					.and()
				.and()

				// --- Spellings the parser normalises, plus a distinct code ------------------
				.withRecord()
				.withID(patientNormalisedCodes)
				.withAgeGroup(0)
				.withValue(PNF, "Katherine").withValue(PNM, "Coleman").withValue(PNL, "Johnson")
					.withVaccination().withAdminAtAgeGroup(0).withReportingGroup("ANY")
						.withValue(CVX, cvxHepBPedBare).withValue(MVX, "MSD")
						.withValue(LOT_NUMBER, "C20001").withValue(EVENT, "00")
					.and()
					.withVaccination().withAdminAtAgeGroup(0).withReportingGroup("ANY")
						.withValue(CVX, cvxHepBPedPadded).withValue(MVX, "MSD")
						.withValue(LOT_NUMBER, "C20002").withValue(EVENT, "00")
					.and()
					.withVaccination().withAdminAtAgeGroup(0).withReportingGroup("ANY")
						.withValue(CVX, cvxInfluenza).withValue(MVX, "SKB")
						.withValue(LOT_NUMBER, "C20003").withValue(EVENT, "00")
					.and()
				.and()

				// --- One code plus a blank one, which must not be counted -------------------
				.withRecord()
				.withID(patientBlankCode)
				.withAgeGroup(0)
				.withValue(PNF, "Ada").withValue(PNM, "Byron").withValue(PNL, "Lovelace")
					.withVaccination().withAdminAtAgeGroup(0).withReportingGroup("ANY")
						.withValue(CVX, cvxMmr).withValue(MVX, "MSD")
						.withValue(LOT_NUMBER, "D30001").withValue(EVENT, "00")
					.and()
					.withVaccination().withAdminAtAgeGroup(0).withReportingGroup("ANY")
						.withValue(MVX, "MSD")
						.withValue(LOT_NUMBER, "D30002").withValue(EVENT, "00")
					.and()
				.and();
		return extractBuilder.get();
	}

	@Override
	public AgeGroupHelper getAgeGroupHelper() {
		return ageGroupHelper;
	}

	public static VaccineCodeReportMock get() {
		return new VaccineCodeReportMock();
	}
}
