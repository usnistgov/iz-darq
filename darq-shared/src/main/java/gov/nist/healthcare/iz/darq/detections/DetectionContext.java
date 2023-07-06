package gov.nist.healthcare.iz.darq.detections;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;


public interface DetectionContext {
	LocalDate getEvaluationDate();
	String calculateAgeGroup(LocalDate dob, LocalDate evaluation);
	String calculateAgeGroupAsOfEvaluationDate(LocalDate dob);
	boolean keepDetection(String detection);
	String obfuscateReportingGroup(String value);
	String getVaccineGroupValue(String code);
	DateTimeFormatter getDateFormatter();
}
