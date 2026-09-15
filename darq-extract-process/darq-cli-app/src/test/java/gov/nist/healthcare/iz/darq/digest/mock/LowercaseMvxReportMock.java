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
	Mixed manufacturer (MVX) code casing. SimpleDigestRunner.processVaccinationManufacturer
	uppercases any non-blank MVX that is not already all-uppercase, and records the ORIGINAL
	(pre-fix) spelling in the map this report reads.

	RECORD 1
		VX 1 => MSD  already uppercase  -> not reported
		VX 2 => msd  all lowercase      -> reported
	RECORD 2
		VX 1 => pfr  all lowercase      -> reported, twice within this one record
		VX 2 => pfr
	RECORD 3
		VX 1 => Skb  mixed case         -> reported
		VX 2 => msd  all lowercase      -> same code as RECORD 1, aggregates across records
		VX 3 => msd
	RECORD 4
		VX 1 => PMC  already uppercase  -> not reported
		VX 2 => (not set)               -> blank, not reported
 */

public class LowercaseMvxReportMock implements DataExtractMock {
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

	static public final String CVX_HEPB_PED = "08";

	// Real HL7 table 0227 manufacturer codes, spelled in the casings the tool has to fix.
	public final String uppercaseMerck = "MSD";       // already correct, must not be reported
	public final String uppercaseSanofi = "PMC";      // already correct, must not be reported
	public final String lowercaseMerck = "msd";       // all lowercase
	public final String lowercasePfizer = "pfr";      // all lowercase, twice in one record
	public final String mixedCaseGsk = "Skb";         // mixed case

	public final String patientUppercaseAndLowercase = "PAT-MSD-MIXED";
	public final String patientRepeatedLowercase = "PAT-PFR-REPEATED";
	public final String patientMixedCase = "PAT-SKB-MIXED-CASE";
	public final String patientNothingToFix = "PAT-NOTHING-TO-FIX";

	public final AgeGroupHelper ageGroupHelper;
	public final ConfigurationPayload configurationPayload;

	protected LowercaseMvxReportMock() {
		ageGroupHelper = new AgeGroupHelper(LocalDate.parse("2020-12-14", DATE_FORMATTER), 3);
		configurationPayload = new ConfigurationPayload();
		configurationPayload.setAsOf("12/14/2020");
		/*
		 * LowercaseMvxReportService declares no required detections (super(FILENAME)), so the
		 * report is produced regardless of what is configured here - the casing fix happens in
		 * SimpleDigestRunner's preprocess, before any detection runs. A realistic manufacturer
		 * detection set is configured anyway; LowercaseMvxReportNoDetectionsTestCase clears it to
		 * show the report is produced either way.
		 */
		configurationPayload.setDetections(Stream.of(
				Detection.VaccinationManufacturerCodeIsMissing,
				Detection.VaccinationManufacturerCodeIsPresent,
				Detection.VaccinationManufacturerCodeIsInvalid,
				Detection.VaccinationManufacturerCodeIsUnrecognized,
				Detection.VaccinationManufacturerCodeIsDeprecated
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

				// --- One already-correct code and one lowercase code -------------------------
				.withRecord()
				.withID(patientUppercaseAndLowercase)
				.withAgeGroup(0)
				.withValue(PNF, "Alan").withValue(PNM, "Mathison").withValue(PNL, "Turing")
					.withVaccination().withAdminAtAgeGroup(0).withReportingGroup("ANY")
						.withValue(CVX, CVX_HEPB_PED).withValue(MVX, uppercaseMerck)
						.withValue(LOT_NUMBER, "A12345").withValue(EVENT, "00")
					.and()
					.withVaccination().withAdminAtAgeGroup(0).withReportingGroup("ANY")
						.withValue(CVX, CVX_HEPB_PED).withValue(MVX, lowercaseMerck)
						.withValue(LOT_NUMBER, "A12346").withValue(EVENT, "00")
					.and()
				.and()

				// --- Same lowercase code twice inside a single record ------------------------
				.withRecord()
				.withID(patientRepeatedLowercase)
				.withAgeGroup(0)
				.withValue(PNF, "Grace").withValue(PNM, "Brewster").withValue(PNL, "Hopper")
					.withVaccination().withAdminAtAgeGroup(0).withReportingGroup("ANY")
						.withValue(CVX, CVX_HEPB_PED).withValue(MVX, lowercasePfizer)
						.withValue(LOT_NUMBER, "B10001").withValue(EVENT, "00")
					.and()
					.withVaccination().withAdminAtAgeGroup(0).withReportingGroup("ANY")
						.withValue(CVX, CVX_HEPB_PED).withValue(MVX, lowercasePfizer)
						.withValue(LOT_NUMBER, "B10002").withValue(EVENT, "00")
					.and()
				.and()

				// --- Mixed case, plus a repeat of RECORD 1's lowercase code ------------------
				.withRecord()
				.withID(patientMixedCase)
				.withAgeGroup(0)
				.withValue(PNF, "Katherine").withValue(PNM, "Coleman").withValue(PNL, "Johnson")
					.withVaccination().withAdminAtAgeGroup(0).withReportingGroup("ANY")
						.withValue(CVX, CVX_HEPB_PED).withValue(MVX, mixedCaseGsk)
						.withValue(LOT_NUMBER, "C20001").withValue(EVENT, "00")
					.and()
					.withVaccination().withAdminAtAgeGroup(0).withReportingGroup("ANY")
						.withValue(CVX, CVX_HEPB_PED).withValue(MVX, lowercaseMerck)
						.withValue(LOT_NUMBER, "C20002").withValue(EVENT, "00")
					.and()
					.withVaccination().withAdminAtAgeGroup(0).withReportingGroup("ANY")
						.withValue(CVX, CVX_HEPB_PED).withValue(MVX, lowercaseMerck)
						.withValue(LOT_NUMBER, "C20003").withValue(EVENT, "00")
					.and()
				.and()

				// --- Nothing to fix: already uppercase, and a blank manufacturer -------------
				.withRecord()
				.withID(patientNothingToFix)
				.withAgeGroup(0)
				.withValue(PNF, "Ada").withValue(PNM, "Byron").withValue(PNL, "Lovelace")
					.withVaccination().withAdminAtAgeGroup(0).withReportingGroup("ANY")
						.withValue(CVX, CVX_HEPB_PED).withValue(MVX, uppercaseSanofi)
						.withValue(LOT_NUMBER, "D30001").withValue(EVENT, "00")
					.and()
					.withVaccination().withAdminAtAgeGroup(0).withReportingGroup("ANY")
						.withValue(CVX, CVX_HEPB_PED)
						.withValue(LOT_NUMBER, "D30002").withValue(EVENT, "00")
					.and()
				.and();
		return extractBuilder.get();
	}

	@Override
	public AgeGroupHelper getAgeGroupHelper() {
		return ageGroupHelper;
	}

	public static LowercaseMvxReportMock get() {
		return new LowercaseMvxReportMock();
	}
}