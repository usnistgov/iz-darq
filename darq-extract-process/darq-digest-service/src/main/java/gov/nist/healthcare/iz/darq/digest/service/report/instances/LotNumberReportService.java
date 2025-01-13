package gov.nist.healthcare.iz.darq.digest.service.report.instances;

import gov.nist.healthcare.iz.darq.detections.RecordDetectionEngineResult;
import gov.nist.healthcare.iz.darq.digest.domain.DetectionSum;
import gov.nist.healthcare.iz.darq.localreport.AggregateLocalReportService;
import gov.nist.healthcare.iz.darq.localreport.AggregateRow;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;
import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;
import org.immregistries.mqe.validator.detection.Detection;

import java.util.*;
import java.util.stream.Collectors;

public class LotNumberReportService extends AggregateLocalReportService {

	public final static String FILENAME = "lot_numbers.csv";
	Set<Detection> DETECTIONS = new HashSet<>(Arrays.asList(
			Detection.VaccinationLotNumberIsInvalid,
			Detection.VaccinationLotNumberFormatIsUnrecognized,
			Detection.VaccinationLotNumberHasInvalidInfixes,
			Detection.VaccinationLotNumberHasInvalidPrefixes,
			Detection.VaccinationLotNumberHasInvalidSuffixes,
			Detection.VaccinationLotNumberHasMultiple,
			Detection.VaccinationLotNumberIsTooShort
	));

	public LotNumberReportService() {
		super(FILENAME);
	}

	@Override
	public List<String> getHeader() {
		return Arrays.asList(
				"Lot Number",
				"CVX",
				"Event Type",
				"Detections",
				"Count"
		);
	}

	@Override
	public List<AggregateRow> getRows(PreProcessRecord context, RecordDetectionEngineResult detections) {
		List<AggregateRow> rows = new ArrayList<>();
		for(VaccineRecord vx: context.getRecord().history) {
			String vaccinationId = vx.vax_event_id.getValue();
			if(vx.lot_number.hasValue()) {
				String lotNumber = vx.lot_number.getValue();
				String cvx = vx.vaccine_type_cvx.hasValue() ? vx.vaccine_type_cvx.getValue() : "";
				String event = vx.event_information_source.hasValue() ? vx.event_information_source.getValue() : "";
				Map<String, DetectionSum> vaccineDetections = detections.getVaccinationDetectionsById().get(vaccinationId);
				List<Detection> lotNumberDetections = getLotNumberDetections(vaccineDetections);
				rows.add(
						new AggregateRow(
								Arrays.asList(
										lotNumber,
										cvx,
										getEventString(event)
								),
								Collections.singletonList(
										lotNumberDetections
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

	List<Detection> getLotNumberDetections(Map<String, DetectionSum> vaccineDetection) {
		List<Detection> lotNumberDetections = new ArrayList<>();
		if(vaccineDetection != null) {
			vaccineDetection.forEach((code, v) -> {
				if(v.exists()) {
					DETECTIONS.forEach((detection) -> {
						if(detectionIs(detection, code)) {
							lotNumberDetections.add(detection);
						}
					});
				}
			});
		}
		return lotNumberDetections;
	}

	String getEventString(String code) {
		if(code == null || code.isEmpty()) {
			return "Not Present";
		}
		if(code.equals("00")) {
			return "Administered";
		}
		if(code.equals("01")) {
			return "Historical";
		}
		return "Unrecognized";
	}

	String getDetectionText(Detection detection) {
		return detection.getMqeMqeCode() + " - " + detection.getDisplayText();
	}

	boolean detectionIs(Detection detection, String code) {
		return detection.mqeCode.name().equals(code);
	}
}
