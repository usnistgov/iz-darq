package gov.nist.healthcare.iz.darq.digest.service.report.instances;

import gov.nist.healthcare.iz.darq.detections.AggregatedRecordDetections;
import gov.nist.healthcare.iz.darq.digest.service.report.AggregateLocalReportService;
import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LotNumberReportService extends AggregateLocalReportService {


	public LotNumberReportService() {
		super("lot_numbers.csv");
	}

	@Override
	public List<String> getHeader() {
		return Arrays.asList(
				"Lot Number",
				"CVX",
				"Detections",
				"Count"
		);
	}

	@Override
	public List<List<String>> getRows(AggregatePatientRecord patientRecord, AggregatedRecordDetections detections) {
		List<List<String>> rows = new ArrayList<>();
		for(VaccineRecord vx: patientRecord.history) {
			if(vx.lot_number.hasValue()) {
				String lotNumber = vx.lot_number.getValue();
				String cvx = vx.vaccine_type_cvx.hasValue() ? vx.vaccine_type_cvx.getValue() : "";
				rows.add(Arrays.asList(
						lotNumber,
						cvx,
						""
				));
			}
		}
		return rows;
	}
}
