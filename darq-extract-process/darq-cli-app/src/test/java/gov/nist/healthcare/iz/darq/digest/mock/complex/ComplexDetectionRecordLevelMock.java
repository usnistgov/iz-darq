package gov.nist.healthcare.iz.darq.digest.mock.complex;

import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.expression.ANDExpression;
import gov.nist.healthcare.iz.darq.digest.domain.expression.ComplexDetection;
import gov.nist.healthcare.iz.darq.digest.domain.expression.ComplexDetectionTarget;
import gov.nist.healthcare.iz.darq.digest.domain.expression.DetectionExpression;
import gov.nist.healthcare.iz.darq.patient.matching.model.PatientMatchingDetection;
import gov.nist.healthcare.iz.darq.test.data.DataExtractMock;
import gov.nist.healthcare.iz.darq.test.helper.AgeGroupHelper;
import gov.nist.healthcare.iz.darq.test.helper.ExtractBuilder;
import gov.nist.healthcare.iz.darq.test.helper.Record;
import org.immregistries.mqe.validator.detection.Detection;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*

	Test about complex detection denominator
		-> Vaccine Lot Number is missing OR Vaccine CVX is missing => Should be only checked on administered
		-> Record with 4 vaccines, 2 Hist, 2 Administered, All records are missing Lot Number and CVX, found should be = 2
	Test about Record Level Detection using Vaccination Detection Codes
		-> Patient address street is present IMPLY Vaccine CVX is unrecognized
		-> 3 records
			- Street Present, 2 vaccinations CVX recognized -> Not triggered
			- Street Missing, 1 vaccination CVX unrecognized -> Not triggered
			- Street Present, 1 vx CVX recognized, 1 vx CVX unrecognized -> Triggered
	Test about Vaccination Level Detection
		-> Vaccine Lot Number is missing OR Vaccine CVX is missing => Should be only checked on administered
		-> Record with 4 vaccines, 4 Administered, All records are missing Lot Number and CVX, found should be = 4
	Test about a complex detection using all operators Record Level
		- AND, OR, XOR, NOT
			-> NOT Patient address street is present OR (Vaccine CVX is unrecognized AND Vaccine MVX is unrecognized)
				- Street Present, 1 vaccinations CVX recognized and MVX unrecognized -> Not triggered
				- Street Present, 1 vaccinations CVX unrecognized and MVX recognized -> Not triggered
				- Street Present, 1 vaccinations CVX recognized and MVX recognized -> Not triggered
				- Street Present, 1 vx CVX recognized, 1 vx CVX recognized -> Triggered
				- Street Missing, 1 vaccination CVX unrecognized and MVX unrecognized -> Not triggered
 */

public class ComplexDetectionRecordLevelMock implements DataExtractMock {
	static private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	static public final int PNF = 1;
	static public final int PNM = 2;
	static public final int PNL = 3;
	static public final int STREET = 10;
	static public String PatientIsPossibleDuplicateAndAddressStreetIsMissing = "DAR0001";
	AgeGroupHelper ageGroupHelper;

	protected ComplexDetectionRecordLevelMock() {
		ageGroupHelper = new AgeGroupHelper(LocalDate.parse("2020-12-14", DATE_FORMATTER), 3);
	}

	@Override
	public ConfigurationPayload getConfigurationPayload() {
		ConfigurationPayload configurationPayload = new ConfigurationPayload();
		configurationPayload.setAsOf("12/14/2020");
		configurationPayload.setDetections(Collections.singletonList("PM001"));
		configurationPayload.setAgeGroups(ageGroupHelper.getAgeGroups());
		configurationPayload.setDetections(Stream.of(
				Detection.PatientAddressStreetIsMissing
		).map(Detection::getMqeMqeCode).collect(Collectors.toList()));
		configurationPayload.getDetections().add(PatientMatchingDetection.PM001.name());
		configurationPayload.setComplexDetections(Arrays.asList(
				getPatientIsPossibleDuplicateAndNameMiddleIsMissing()
		));
		configurationPayload.setActivatePatientMatching(true);
		configurationPayload.setVaxCodeAbstraction(null);
		return configurationPayload;
	}

	public ComplexDetection getPatientIsPossibleDuplicateAndNameMiddleIsMissing() {
		ComplexDetection complexDetection = new ComplexDetection();
		complexDetection.setCode(PatientIsPossibleDuplicateAndAddressStreetIsMissing);
		complexDetection.setDescription("Patient is possible duplicate and address street is missing");
		complexDetection.setTarget(ComplexDetectionTarget.RECORD);
		ANDExpression andExpression = new ANDExpression();
		andExpression.setExpressions(Arrays.asList(
				new DetectionExpression(Detection.PatientAddressStreetIsMissing.getMqeMqeCode()),
				new DetectionExpression(PatientMatchingDetection.PM001.name())
		));
		complexDetection.setExpression(andExpression);
		return complexDetection;
	}

	@Override
	public List<Record> getDataExtract() {
		return new ExtractBuilder(ageGroupHelper)
				.withRecord()
				// Record 1
				.withAgeGroup(0)
					.withValue(PNF, "Alan")
					.withValue(PNM, "Mathison")
					.withValue(STREET, "")
					.withValue(PNL, "Turing")
					.withVocabulary("GENDER_0001", "M")
				.and()
				.withRecord()
				// Record 2
				.withAgeGroup(0)
					.withValue(PNF, "Alan")
					.withValue(PNM, "Mathison")
					.withValue(PNL, "Turing")
					.withValue(STREET, "")
					.withVocabulary("GENDER_0001", "M")
				.and()
				.withRecord()
				// Record 3
				.withAgeGroup(1)
					.withValue(PNF, "Ada")
					.withValue(PNM, "Augusta")
					.withValue(PNL, "Lovelace")
					.withValue(STREET, "123 Main Street")
				.withVocabulary("GENDER_0001", "F")
				.and()
				.withRecord()
				// Record 4
				.withAgeGroup(1)
					.withValue(PNF, "Ada")
					.withValue(PNM, "Augusta")
					.withValue(PNL, "Lovelace")
					.withValue(STREET, "123 Main Street")
				.withVocabulary("GENDER_0001", "F")
				.and()
				.withRecord()
				// Record 5
				.withAgeGroup(0)
					.withValue(PNF, "Testy")
					.withValue(PNM, "Teston")
					.withValue(PNL, "McTester")
					.withValue(STREET, "123 Main Street")
				.withVocabulary("GENDER_0001", "M")
				.and()
				.get();
	}

	@Override
	public AgeGroupHelper getAgeGroupHelper() {
		return ageGroupHelper;
	}

	public static ComplexDetectionRecordLevelMock get() {
		return new ComplexDetectionRecordLevelMock();
	}
}
