package gov.nist.healthcare.iz.darq.digest.mock.complex;

import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.expression.*;
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

/*
	Test about a complex detection using all operators Record Level
		- AND, OR, XOR, NOT
			-> NOT Patient address street is present OR (Vaccine CVX is unrecognized AND Vaccine MVX is unrecognized)
				- Street Present, 1 vaccinations CVX recognized and MVX unrecognized -> Not triggered
				- Street Present, 1 vaccinations CVX unrecognized and MVX recognized -> Not triggered
				- Street Present, 1 vaccinations CVX recognized and MVX recognized -> Not triggered
				- Street Present, 1 vx CVX recognized, 1 vx CVX recognized -> Triggered
				- Street Missing, 1 vaccination CVX unrecognized and MVX unrecognized -> Not triggered
 */
public class ComplexDetectionMultiOpMock implements DataExtractMock {
	static private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	AgeGroupHelper ageGroupHelper = new AgeGroupHelper(LocalDate.parse("2020-12-14", DATE_FORMATTER), 3);
	public static String DETECTION_CODE = "DAR0001";
	public static ComplexDetectionTarget TARGET = ComplexDetectionTarget.RECORD;
	static public final int CVX = 7;
	static public final int MVX = 10;
	static public final int LOT_NUMBER = 11;
	static public final int EVENT = 12;
	static public final int PNF = 1;
	static public final int PNM = 2;
	static public final int PNL = 3;
	static public final int STREET = 10;

	@Override
	public ConfigurationPayload getConfigurationPayload() {
		ConfigurationPayload configurationPayload = new ConfigurationPayload();
		configurationPayload.setAsOf("12/14/2020");
		configurationPayload.setDetections(Collections.singletonList("PM001"));
		configurationPayload.setAgeGroups(ageGroupHelper.getAgeGroups());
		ComplexDetection complexDetection = new ComplexDetection();
		complexDetection.setCode(DETECTION_CODE);
		complexDetection.setDescription("Complex detection");
		complexDetection.setTarget(TARGET);
		complexDetection.setExpression(getExpression());
		configurationPayload.setComplexDetections(Arrays.asList(
				complexDetection
		));
		configurationPayload.setActivatePatientMatching(false);
		configurationPayload.setVaxCodeAbstraction(null);
		return configurationPayload;
	}

	public Expression getExpression() {
		// 			-> NOT Patient address street is present OR (Vaccine CVX is unrecognized AND Vaccine MVX is unrecognized)
		NOTExpression notExpression = new NOTExpression();
		notExpression.setExpression(new DetectionExpression(Detection.PatientAddressStreetIsPresent.getMqeMqeCode()));

		ANDExpression andExpression = new ANDExpression();
		andExpression.setExpressions(Arrays.asList(
				new DetectionExpression(Detection.VaccinationCvxCodeIsUnrecognized.getMqeMqeCode()),
				new DetectionExpression(Detection.VaccinationManufacturerCodeIsUnrecognized.getMqeMqeCode())
		));

		ORExpression orExpression = new ORExpression();
		orExpression.setExpressions(Arrays.asList(
				notExpression,
				andExpression
		));

		return orExpression;
	}

	@Override
	public List<Record> getDataExtract() {
		/*
		-> 3 records
				- Street Present, 1 vaccinations CVX recognized and MVX unrecognized -> Not triggered
				- Street Present, 1 vaccinations CVX unrecognized and MVX recognized -> Not triggered
				- Street Present, 1 vaccinations CVX recognized and MVX recognized -> Not triggered
				- Street Present, 1 vaccination CVX unrecognized and MVX unrecognized -> triggered
				- Street Missing, 1 vaccination CVX unrecognized and MVX unrecognized -> Not triggered
		 */
		return new ExtractBuilder(ageGroupHelper)
				.withRecord()
				// Record 1
				.withAgeGroup(0)
				.withValue(STREET, "123 main street")
				.withVaccination()
				// Vaccination 1
				.withAdminAtAgeGroup(0)
				.withReportingGroup("ANY")
				.withValue(EVENT, "00")
				.withValue(CVX, "145")
				.withValue(MVX, "ZZZ")
				.and()
				.and().withRecord()
				// Record 2
				.withAgeGroup(0)
				.withValue(STREET, "123 main street")
				.withVaccination()
				// Vaccination 1
				.withAdminAtAgeGroup(0)
				.withReportingGroup("ANY")
				.withValue(EVENT, "00")
				.withValue(CVX, "953")
				.withValue(MVX, "AB")
				.and()
				.and().withRecord()
				// Record 3
				.withAgeGroup(0)
				.withValue(STREET, "123 main street")
				.withVaccination()
				// Vaccination 1
				.withAdminAtAgeGroup(0)
				.withReportingGroup("ANY")
				.withValue(EVENT, "00")
				.withValue(CVX, "145")
				.withValue(MVX, "AB")
				.and()
				.and().withRecord()
				// Record 4
				.withAgeGroup(0)
				.withValue(STREET, "123 main street")
				.withVaccination()
				// Vaccination 1
				.withAdminAtAgeGroup(0)
				.withReportingGroup("ANY")
				.withValue(EVENT, "00")
				.withValue(CVX, "953")
				.withValue(MVX, "ZZZ")
				.and()
				.and().withRecord()
				// Record 5
				.withAgeGroup(0)
				.withValue(STREET, "")
				.withVaccination()
				// Vaccination 1
				.withAdminAtAgeGroup(0)
				.withReportingGroup("ANY")
				.withValue(EVENT, "00")
				.withValue(CVX, "953")
				.withValue(MVX, "ZZZ")
				.and()
				.and().get();
	}

	@Override
	public AgeGroupHelper getAgeGroupHelper() {
		return ageGroupHelper;
	}
}
