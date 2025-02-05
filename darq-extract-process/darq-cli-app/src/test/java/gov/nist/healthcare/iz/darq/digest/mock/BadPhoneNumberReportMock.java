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

	RECORD 1
		800-544-5555 (Valid)
	RECORD 5
		800-555-12121 (Invalid)
	RECORD 2
		555-1212 (Invalid)
	RECORD 3
		800-555-ABCD (Invalid)
	RECORD 4
		000-555-1212 (Invalid)
	RECORD 5
		800-555-12121 (Invalid)
	RECORD 6
		800-555-ABCD (Invalid)

 */

public class BadPhoneNumberReportMock implements DataExtractMock {
	static private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	static public final int PNF = 1;
	static public final int PNM = 2;
	static public final int PNL = 3;
	static public final int PHONE = 17;
	public final String validPhone = "800-544-5555";
	public final String invalidPhoneLong = "800-555-12121";
	public final String invalidPhoneShort = "555-1212";
	public final String invalidPhoneFormat = "800-555-ABCD";
	public final String invalidPhoneArea = "000-555-1212";
	public final AgeGroupHelper ageGroupHelper;
	public final ConfigurationPayload configurationPayload;

	protected BadPhoneNumberReportMock() {
		ageGroupHelper = new AgeGroupHelper(LocalDate.parse("2020-12-14", DATE_FORMATTER), 3);
		configurationPayload = new ConfigurationPayload();
		configurationPayload.setAsOf("12/14/2020");
		configurationPayload.setDetections(Stream.of(
				Detection.PatientPhoneIsIncomplete,
				Detection.PatientPhoneIsInvalid,
				Detection.PatientPhoneIsMissing,
				Detection.PatientPhoneIsPresent,
				Detection.PatientPhoneTelUseCodeIsDeprecated,
				Detection.PatientPhoneTelUseCodeIsInvalid,
				Detection.PatientPhoneTelUseCodeIsMissing,
				Detection.PatientPhoneTelUseCodeIsPresent,
				Detection.PatientPhoneTelUseCodeIsUnrecognized,
				Detection.PatientPhoneTelEquipCodeIsDeprecated,
				Detection.PatientPhoneTelEquipCodeIsInvalid,
				Detection.PatientPhoneTelEquipCodeIsMissing,
				Detection.PatientPhoneTelEquipCodeIsPresent,
				Detection.PatientPhoneTelEquipCodeIsUnrecognized
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
		/*
		800-544-5555 (Valid)
		800-555-12121 (Invalid)
		555-1212 (Invalid)
		800-555-ABCD (Invalid)
		000-555-1212 (Invalid)
		800-555-12121 (Invalid)
		800-555-ABCD (Invalid)
		"" (Empty)
		 */
		ExtractBuilder extractBuilder = new ExtractBuilder(ageGroupHelper)
				.withRecord()
				.withAgeGroup(0)
				.withValue(PNF, "Alan").withValue(PNM, "Mathison").withValue(PNL, "Turing").withValue(PHONE, validPhone)
				.and()
				.withRecord()
				.withAgeGroup(0)
				.withValue(PNF, "Alan").withValue(PNM, "Mathison").withValue(PNL, "Turing").withValue(PHONE, invalidPhoneLong)
				.and()
				.withRecord()
				.withAgeGroup(0)
				.withValue(PNF, "Alan").withValue(PNM, "Mathison").withValue(PNL, "Turing").withValue(PHONE, invalidPhoneShort)
				.and()
				.withRecord()
				.withAgeGroup(0)
				.withValue(PNF, "Alan").withValue(PNM, "Mathison").withValue(PNL, "Turing").withValue(PHONE, invalidPhoneFormat)
				.and()
				.withRecord()
				.withAgeGroup(0)
				.withValue(PNF, "Alan").withValue(PNM, "Mathison").withValue(PNL, "Turing").withValue(PHONE, invalidPhoneArea)
				.and()
				.withRecord()
				.withAgeGroup(0)
				.withValue(PNF, "Alan").withValue(PNM, "Mathison").withValue(PNL, "Turing").withValue(PHONE, invalidPhoneLong)
				.and()
				.withRecord()
				.withAgeGroup(0)
				.withValue(PNF, "Alan").withValue(PNM, "Mathison").withValue(PNL, "Turing").withValue(PHONE, invalidPhoneFormat)
				.and()
				.withRecord()
				.withAgeGroup(0)
				.withValue(PNF, "Alan").withValue(PNM, "Mathison").withValue(PNL, "Turing").withValue(PHONE, "")
				.and();
		return extractBuilder.get();
	}

	@Override
	public AgeGroupHelper getAgeGroupHelper() {
		return ageGroupHelper;
	}

	public static BadPhoneNumberReportMock get() {
		return new BadPhoneNumberReportMock();
	}
}
