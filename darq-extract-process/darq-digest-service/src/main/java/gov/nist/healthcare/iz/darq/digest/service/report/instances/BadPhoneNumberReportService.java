package gov.nist.healthcare.iz.darq.digest.service.report.instances;

import gov.nist.healthcare.iz.darq.detections.RecordDetectionEngineResult;
import gov.nist.healthcare.iz.darq.digest.domain.DetectionSum;
import gov.nist.healthcare.iz.darq.digest.service.report.AggregateLocalReportService;
import gov.nist.healthcare.iz.darq.digest.service.report.model.AggregateRow;
import gov.nist.healthcare.iz.darq.parser.type.DqString;
import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;
import org.immregistries.mqe.validator.detection.Detection;

import java.util.*;
import java.util.stream.Collectors;

public class BadPhoneNumberReportService extends AggregateLocalReportService {

	public static final String FILENAME = "bad_phone_number.csv";
	Set<Detection> DETECTIONS = new HashSet<>(Arrays.asList(
			Detection.PatientPhoneIsIncomplete,
			Detection.PatientPhoneIsInvalid,
		    Detection.PatientPhoneTelUseCodeIsDeprecated,
		    Detection.PatientPhoneTelUseCodeIsInvalid,
			Detection.PatientPhoneTelUseCodeIsUnrecognized,
			Detection.PatientPhoneTelEquipCodeIsDeprecated,
		    Detection.PatientPhoneTelEquipCodeIsInvalid,
			Detection.PatientPhoneTelEquipCodeIsUnrecognized
	));

	public BadPhoneNumberReportService() {
		super(FILENAME);
	}

	@Override
	public List<String> getHeader() {
		return Arrays.asList(
				"Phone Number",
				"Detections",
				"Count"
		);
	}

	@Override
	public List<AggregateRow> getRows(PreProcessRecord context, RecordDetectionEngineResult detections) {
		List<AggregateRow> rows = new ArrayList<>();
		boolean hasBadPhoneNumber = DETECTIONS.stream()
												.map(Detection::getMqeMqeCode)
		                                       .anyMatch((detection) -> detections.getPatientDetections().containsKey(detection) && detections.getPatientDetections().get(detection).exists());
		if (hasBadPhoneNumber) {
			DqString phone = context.getRecord().patient.phone;
			if(phone.hasValue()) {
				List<Detection> phoneNumberDetections = getPhoneNumberDetections(
						detections.getPatientDetections()
				);
				rows.add(
						new AggregateRow(
								Collections.singletonList(
										phone.getValue()
								),
								Collections.singletonList(
										phoneNumberDetections
												.stream()
												.map(this::getDetectionText)
												.collect(Collectors.joining(" "))
								)
						)
				);
			}
		}

		return rows;
	}

	List<Detection> getPhoneNumberDetections(Map<String, DetectionSum> patientDetections) {
		List<Detection> phoneNumberDetections = new ArrayList<>();
		if(patientDetections != null) {
			patientDetections.forEach((code, v) -> {
				if(v.exists()) {
					DETECTIONS.forEach((detection) -> {
						if(detectionIs(detection, code)) {
							phoneNumberDetections.add(detection);
						}
					});
				}
			});
		}
		return phoneNumberDetections;
	}

	String getDetectionText(Detection detection) {
		return detection.getMqeMqeCode() + " - " + detection.getDisplayText();
	}

	boolean detectionIs(Detection detection, String code) {
		return detection.mqeCode.name().equals(code);
	}
}
