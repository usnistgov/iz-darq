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
		20899 (Valid)
	RECORD 2
		1234 (Invalid - length != 5)
	RECORD 3
		208999 (Invalid - length != 5)
	RECORD 4
		00000 (Invalid - repeated digits)
	RECORD 5
		99999 (Invalid - repeated digits)
	RECORD 6
		00100 (Invalid - lower than 00501)
	RECORD 7
		99999 (Invalid - greater than 99950) --> same value as RECORD 5, aggregates to 2
	RECORD 8
		"" (Empty) --> not reported

 */

public class BadZipCodeReportMock implements DataExtractMock {
	static private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	static public final int PNF = 1;
	static public final int PNM = 2;
	static public final int PNL = 3;
	// Patient.address is at index 10, Address.street is at sub-index 0, Address.zip at sub-index 4
	static public final int STREET = 10;
	static public final int ZIP = 14;

	/*
	 * MqeAddress.equals/hashCode only consider street/street2/city/state - zip and country are
	 * deliberately excluded. MessageTransformer runs every address in the message (patient,
	 * responsible party, next-of-kin) through AddressCleanser, which collects them into a
	 * HashMap<MqeAddress, MqeAddress>. If the patient address has no street/city/state it compares
	 * equal to the blank responsible-party and next-of-kin addresses, they collapse into a single
	 * map entry, and the patient's address is replaced by a blank one - silently dropping the zip
	 * before any validation rule sees it. Giving the patient address a street keeps it distinct.
	 */
	public final String street = "100 Bureau Dr";

	public final String validZip = "20899";
	public final String invalidZipTooShort = "1234";
	public final String invalidZipTooLong = "208999";
	public final String invalidZipRepeatedLow = "00000";
	public final String invalidZipRepeatedHigh = "99999";
	public final String invalidZipBelowRange = "00100";
	public final String invalidZipAboveRange = "99960";

	public final AgeGroupHelper ageGroupHelper;
	public final ConfigurationPayload configurationPayload;

	protected BadZipCodeReportMock() {
		ageGroupHelper = new AgeGroupHelper(LocalDate.parse("2020-12-14", DATE_FORMATTER), 3);
		configurationPayload = new ConfigurationPayload();
		configurationPayload.setAsOf("12/14/2020");
		configurationPayload.setDetections(Stream.of(
				Detection.PatientAddressZipIsInvalid,
				Detection.PatientAddressZipIsMissing,
				Detection.PatientAddressZipIsPresent
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
		20899  (Valid)
		1234   (Invalid - length != 5)
		208999 (Invalid - length != 5)
		00000  (Invalid - repeated digits)
		99999  (Invalid - repeated digits)
		00100  (Invalid - lower than 00501)
		99999  (Invalid - greater than 99950)
		""     (Empty)
		 */
		ExtractBuilder extractBuilder = new ExtractBuilder(ageGroupHelper)
				.withRecord()
				.withAgeGroup(0)
				.withValue(PNF, "Alan").withValue(PNM, "Mathison").withValue(PNL, "Turing").withValue(STREET, street).withValue(ZIP, validZip)
				.and()
				.withRecord()
				.withAgeGroup(0)
				.withValue(PNF, "Alan").withValue(PNM, "Mathison").withValue(PNL, "Turing").withValue(STREET, street).withValue(ZIP, invalidZipTooShort)
				.and()
				.withRecord()
				.withAgeGroup(0)
				.withValue(PNF, "Alan").withValue(PNM, "Mathison").withValue(PNL, "Turing").withValue(STREET, street).withValue(ZIP, invalidZipTooLong)
				.and()
				.withRecord()
				.withAgeGroup(0)
				.withValue(PNF, "Alan").withValue(PNM, "Mathison").withValue(PNL, "Turing").withValue(STREET, street).withValue(ZIP, invalidZipRepeatedLow)
				.and()
				.withRecord()
				.withAgeGroup(0)
				.withValue(PNF, "Alan").withValue(PNM, "Mathison").withValue(PNL, "Turing").withValue(STREET, street).withValue(ZIP, invalidZipRepeatedHigh)
				.and()
				.withRecord()
				.withAgeGroup(0)
				.withValue(PNF, "Alan").withValue(PNM, "Mathison").withValue(PNL, "Turing").withValue(STREET, street).withValue(ZIP, invalidZipBelowRange)
				.and()
				.withRecord()
				.withAgeGroup(0)
				.withValue(PNF, "Alan").withValue(PNM, "Mathison").withValue(PNL, "Turing").withValue(STREET, street).withValue(ZIP, invalidZipAboveRange)
				.and()
				.withRecord()
				.withAgeGroup(0)
				.withValue(PNF, "Alan").withValue(PNM, "Mathison").withValue(PNL, "Turing").withValue(STREET, street).withValue(ZIP, "")
				.and();
		return extractBuilder.get();
	}

	@Override
	public AgeGroupHelper getAgeGroupHelper() {
		return ageGroupHelper;
	}

	public static BadZipCodeReportMock get() {
		return new BadZipCodeReportMock();
	}
}
