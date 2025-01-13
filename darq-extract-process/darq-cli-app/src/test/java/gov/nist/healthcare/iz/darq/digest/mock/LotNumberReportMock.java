package gov.nist.healthcare.iz.darq.digest.mock;

import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.test.data.DataExtractMock;
import gov.nist.healthcare.iz.darq.test.helper.AgeGroupHelper;
import gov.nist.healthcare.iz.darq.test.helper.ExtractBuilder;
import gov.nist.healthcare.iz.darq.test.helper.Record;
import org.immregistries.mqe.validator.detection.Detection;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*

	RECORD 1
		VX 1 => 08 VALID
		VX 2 => 145 VALID
		VX 3 => 08 INVALID
		VX 4 => 145 INVALID
		VX 5 => 08 PREFIX
	RECORD 2
		VX 1 => 08 SUFFIX
		VX 2 => 145 PREFIX
		VX 3 => 08 VALID
		VX 4 => 08 PREFIX
		VX 5 => 145 INFIX
	RECORD 3
		VX 1 => 8 VALID 01
		VX 2 => 145 INVALID 01

 */

public class LotNumberReportMock implements DataExtractMock {
	static private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	static public final int LOT_NUMBER = 11;
	static public final int CVX = 7;
	static public final int PNF = 1;
	static public final int PNM = 2;
	static public final int PNL = 3;
	static public final int EVENT = 12;
	public String validLotNumber = "A12345";
	public String lotNumberInvalid = "!!!!!!!!";
	public String suffix = "A12345-V";
	public String prefix = "PMCA12345";
	public String infix = "A12345ICE35677";


	AgeGroupHelper ageGroupHelper;

	protected LotNumberReportMock() {
		ageGroupHelper = new AgeGroupHelper(LocalDate.parse("2020-12-14", DATE_FORMATTER), 3);
	}

	@Override
	public ConfigurationPayload getConfigurationPayload() {
		ConfigurationPayload configurationPayload = new ConfigurationPayload();
		configurationPayload.setAsOf("12/14/2020");
		configurationPayload.setDetections(Stream.of(
				Detection.VaccinationLotNumberIsInvalid,
				Detection.VaccinationLotNumberFormatIsUnrecognized,
				Detection.VaccinationLotNumberHasInvalidInfixes,
				Detection.VaccinationLotNumberHasInvalidPrefixes,
				Detection.VaccinationLotNumberHasInvalidSuffixes,
				Detection.VaccinationLotNumberHasMultiple,
				Detection.VaccinationLotNumberIsTooShort
		).map(Detection::getMqeMqeCode).collect(Collectors.toList()));
		configurationPayload.setAgeGroups(ageGroupHelper.getAgeGroups());
		configurationPayload.setActivatePatientMatching(false);
		configurationPayload.setVaxCodeAbstraction(null);
		return configurationPayload;
	}

	@Override
	public List<Record> getDataExtract() {
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
						.withValue(EVENT, "00")
						.withValue(LOT_NUMBER, validLotNumber)
					.and()
					.withVaccination()
						.withAdminAtAgeGroup(0)
						.withReportingGroup("ANY")
						.withValue(CVX, "145")
						.withValue(EVENT, "00")
						.withValue(LOT_NUMBER, validLotNumber)
					.and()
					.withVaccination()
						.withAdminAtAgeGroup(0)
						.withReportingGroup("ANY")
						.withValue(CVX, "08")
						.withValue(EVENT, "00")
						.withValue(LOT_NUMBER, lotNumberInvalid)
					.and()
					.withVaccination()
						.withAdminAtAgeGroup(0)
						.withReportingGroup("ANY")
						.withValue(CVX, "145")
						.withValue(EVENT, "00")
						.withValue(LOT_NUMBER, lotNumberInvalid)
					.and()
					.withVaccination()
						.withAdminAtAgeGroup(0)
						.withReportingGroup("ANY")
						.withValue(CVX, "08")
						.withValue(EVENT, "00")
						.withValue(LOT_NUMBER, prefix)
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
					.withValue(EVENT, "00")
					.withValue(LOT_NUMBER, suffix)
				.and()
				.withVaccination()
					.withAdminAtAgeGroup(0)
					.withReportingGroup("ANY")
					.withValue(CVX, "145")
					.withValue(EVENT, "00")
					.withValue(LOT_NUMBER, prefix)
				.and()
				.withVaccination()
					.withAdminAtAgeGroup(0)
					.withReportingGroup("ANY")
					.withValue(CVX, "08")
					.withValue(EVENT, "00")
					.withValue(LOT_NUMBER, validLotNumber)
				.and()
				.withVaccination()
					.withAdminAtAgeGroup(0)
					.withReportingGroup("ANY")
					.withValue(CVX, "08")
					.withValue(EVENT, "00")
					.withValue(LOT_NUMBER, prefix)
				.and()
				.withVaccination()
					.withAdminAtAgeGroup(0)
					.withReportingGroup("ANY")
					.withValue(CVX, "145")
					.withValue(EVENT, "00")
					.withValue(LOT_NUMBER, infix)
					.and()
				.and()
				.withRecord()
				// Record 2
				.withAgeGroup(0)
				.withValue(PNF, "Albert")
				.withValue(PNL, "Einstein")
				.withVocabulary("GENDER_0001", "M")
				.withVaccination()
					.withAdminAtAgeGroup(0)
					.withReportingGroup("ANY")
					.withValue(CVX, "08")
					.withValue(EVENT, "01")
					.withValue(LOT_NUMBER, validLotNumber)
				.and()
				.withVaccination()
					.withAdminAtAgeGroup(0)
					.withReportingGroup("ANY")
					.withValue(CVX, "145")
					.withValue(EVENT, "01")
					.withValue(LOT_NUMBER, lotNumberInvalid)
				.and().and();
		return extractBuilder.get();
	}

	@Override
	public AgeGroupHelper getAgeGroupHelper() {
		return ageGroupHelper;
	}



	public static LotNumberReportMock get() {
		return new LotNumberReportMock();
	}
}
