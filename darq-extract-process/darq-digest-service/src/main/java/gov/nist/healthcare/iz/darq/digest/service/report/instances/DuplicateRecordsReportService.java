package gov.nist.healthcare.iz.darq.digest.service.report.instances;

import gov.nist.healthcare.iz.darq.detections.RecordDetectionEngineResult;
import gov.nist.healthcare.iz.darq.digest.service.report.SimpleLocalReportService;
import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;

import java.util.*;

public class DuplicateRecordsReportService extends SimpleLocalReportService {

	public final static String FILENAME = "possible_duplicate_records.csv";

	public DuplicateRecordsReportService() {
		super(FILENAME);
	}

	@Override
	public List<String> getHeader() {
		return Arrays.asList(
				"Record Id",
				"Duplicate Record Id",
				"MISMO Match Signature"
		);
	}

	@Override
	public List<List<String>> getRows(PreProcessRecord context, RecordDetectionEngineResult recordDetectionEngineResult) {
		Map<String, String> duplicates = recordDetectionEngineResult.getPossiblePatientRecordDuplicatesWithSignature();
		List<List<String>> rows = new ArrayList<>();
		if(duplicates != null && !duplicates.isEmpty()) {
			for(String duplicateId : duplicates.keySet()) {
				rows.add(
						Arrays.asList(
								context.getRecord().patient.getID(),
								duplicateId,
								duplicates.get(duplicateId)
						)
				);
			}
		}
		return rows;
	}
}
