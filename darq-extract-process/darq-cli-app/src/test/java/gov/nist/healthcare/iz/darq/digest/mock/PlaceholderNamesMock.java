package gov.nist.healthcare.iz.darq.digest.mock;

import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.test.data.DataExtractMock;
import gov.nist.healthcare.iz.darq.test.helper.AgeGroupHelper;
import gov.nist.healthcare.iz.darq.test.helper.ExtractBuilder;
import gov.nist.healthcare.iz.darq.test.helper.Record;
import org.immregistries.mqe.validator.detection.Detection;
import org.immregistries.mqe.validator.engine.codes.KnownName;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PlaceholderNamesMock implements DataExtractMock {
	static private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	static public final int PNF = 1;
	static public final int PNM = 2;
	static public final int PNL = 3;
	public Map<KnownName, Integer> names = new HashMap<>();
	String PLACEHOLDER = "Jack";

	AgeGroupHelper ageGroupHelper;

	protected PlaceholderNamesMock() {
		ageGroupHelper = new AgeGroupHelper(LocalDate.parse("2020-12-14", DATE_FORMATTER), 3);
	}

	@Override
	public ConfigurationPayload getConfigurationPayload() {
		ConfigurationPayload configurationPayload = new ConfigurationPayload();
		configurationPayload.setAsOf("12/14/2020");
		configurationPayload.setDetections(Arrays.asList(
				Detection.PatientNameHasJunkName.getMqeMqeCode(),
				Detection.PatientNameMayBeTestName.getMqeMqeCode(),
				Detection.PatientNameMayBeTemporaryNewbornName.getMqeMqeCode(),
				Detection.PatientNameMiddleIsInvalid.getMqeMqeCode(),
				Detection.PatientNameLastIsInvalid.getMqeMqeCode(),
				Detection.PatientNameFirstIsInvalid.getMqeMqeCode()
		));
		configurationPayload.setAgeGroups(ageGroupHelper.getAgeGroups());
		configurationPayload.setActivatePatientMatching(false);
		configurationPayload.setVaxCodeAbstraction(null);
		return configurationPayload;
	}

	@Override
	public List<Record> getDataExtract() {
		this.names = new HashMap<>();
		ExtractBuilder extractBuilder = new ExtractBuilder(ageGroupHelper)
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
				.withValue(PNF, "Ada")
				.withValue(PNM, "Augusta")
				.withValue(PNL, "Lovelace")
				.withVocabulary("GENDER_0001", "F").and();

		names.put(
				new KnownName(24, "BOY", "BABY", "", null, KnownName.NameType.UNNAMED_NEWBORN),
				3
		);
		names.put(
				new KnownName(26, "MOUSE", "MICKY", "", null, KnownName.NameType.TEST_PATIENT),
				1
		);
		names.put(
				new KnownName(53, "", "NOFIRSTNAME", "", null, KnownName.NameType.JUNK_NAME),
				4
		);
		names.put(
				new KnownName(74, "", "BABY", "", null, KnownName.NameType.JUNK_NAME),
				2
		);
		names.put(
				new KnownName(22, "", "BABY", "", null, KnownName.NameType.UNNAMED_NEWBORN),
				4
		);


		for(KnownName knownName : names.keySet()) {
			for(int i = 0; i < names.get(knownName); i++) {
				extractBuilder.withRecord()
				              .withAgeGroup(0)
						      .withValue(PNF, valueOrPlaceholder(knownName.getNameFirst()))
						      .withValue(PNM, valueOrPlaceholder(knownName.getNameMiddle()))
						      .withValue(PNL, valueOrPlaceholder(knownName.getNameLast()))
						      .withVocabulary("GENDER_0001", "F")
						      .and();
			}
		}
		return extractBuilder.get();
	}

	public String valueOrPlaceholder(String value) {
		return value.isEmpty() ? PLACEHOLDER : value;
	}

	@Override
	public AgeGroupHelper getAgeGroupHelper() {
		return ageGroupHelper;
	}

	public static PlaceholderNamesMock get() {
		return new PlaceholderNamesMock();
	}
}
