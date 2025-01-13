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
	Test about Vaccination Level Detection
		-> Vaccine Lot Number is missing OR Vaccine CVX is missing => Should be only checked on administered
		-> Record with 4 vaccines, 4 Administered, All records are missing Lot Number and CVX, found should be = 4
 */
public class ComplexDetectionVaccinationLevelMock implements DataExtractMock {
	static private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	AgeGroupHelper ageGroupHelper = new AgeGroupHelper(LocalDate.parse("2020-12-14", DATE_FORMATTER), 3);
	public static String DETECTION_CODE = "DAR0001";
	public static ComplexDetectionTarget TARGET = ComplexDetectionTarget.VACCINATION;
	static public final int CVX = 7;
	static public final int LOT_NUMBER = 11;
	static public final int EVENT = 12;

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
		configurationPayload.setActivatePatientMatching(true);
		configurationPayload.setVaxCodeAbstraction(null);
		return configurationPayload;
	}

	public Expression getExpression() {
		ORExpression orExpression = new ORExpression();
		orExpression.setExpressions(Arrays.asList(
				new DetectionExpression(Detection.VaccinationLotNumberIsMissing.getMqeMqeCode()),
				new DetectionExpression(Detection.VaccinationCvxCodeIsMissing.getMqeMqeCode())
		));
		return orExpression;
	}

	@Override
	public List<Record> getDataExtract() {
		// Record with 4 vaccines, 4 Administered, All records are missing Lot Number and CVX, found should be = 4
		return new ExtractBuilder(ageGroupHelper)
				.withRecord()
				.withAgeGroup(0)
				.withVaccination()
				// Vaccination 1
				.withAdminAtAgeGroup(0)
				.withReportingGroup("ANY")
				.withValue(LOT_NUMBER, "")
				.withValue(CVX, "")
				.withValue(EVENT, "00")
				.and().withVaccination()
				// Vaccination 1
				.withAdminAtAgeGroup(0)
				.withReportingGroup("ANY")
				.withValue(LOT_NUMBER, "")
				.withValue(CVX, "")
				.withValue(EVENT, "00")
				.and().withVaccination()
				// Vaccination 1
				.withAdminAtAgeGroup(0)
				.withReportingGroup("ANY")
				.withValue(LOT_NUMBER, "")
				.withValue(CVX, "")
				.withValue(EVENT, "00")
				.and().withVaccination()
				// Vaccination 1
				.withAdminAtAgeGroup(0)
				.withReportingGroup("ANY")
				.withValue(LOT_NUMBER, "")
				.withValue(CVX, "")
				.withValue(EVENT, "00")
				.and().and().get();
	}

	@Override
	public AgeGroupHelper getAgeGroupHelper() {
		return ageGroupHelper;
	}
}
