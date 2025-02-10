package gov.nist.healthcare.iz.darq.digest.service.report.instances;

import gov.nist.healthcare.iz.darq.detections.AvailableDetectionEngines;
import gov.nist.healthcare.iz.darq.detections.DetectionEngine;
import gov.nist.healthcare.iz.darq.detections.RecordDetectionEngineResult;
import gov.nist.healthcare.iz.darq.localreport.LocalReportEngineConfiguration;
import gov.nist.healthcare.iz.darq.localreport.SimpleLocalReportService;
import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;

import java.util.*;

public class DuplicateVaccinationReportService extends SimpleLocalReportService {

	public final static String FILENAME = "possible_duplicate_vaccinations.csv";

	public DuplicateVaccinationReportService() {
		super(FILENAME);
	}

	@Override
	public List<String> getHeader() {
		return Arrays.asList(
				"Record Id",
				"Vaccination Id",
				"Duplicate Vaccination Id"
		);
	}

	@Override
	public List<List<String>> getRows(PreProcessRecord context, RecordDetectionEngineResult recordDetectionEngineResult) {
		Map<String, Set<String>> duplicates = recordDetectionEngineResult.getPossibleVaccinationDuplicates();
		List<List<String>> rows = new ArrayList<>();
		if(duplicates != null && !duplicates.isEmpty()) {
			for(String vaccinationId : duplicates.keySet()) {
				for(String duplicateId : duplicates.get(vaccinationId)) {
					rows.add(
							Arrays.asList(
									context.getRecord().patient.getID(),
									vaccinationId,
									duplicateId
							)
					);
				}
			}
		}
		return rows;
	}

	@Override
	public boolean handleInclude(LocalReportEngineConfiguration configuration, DetectionEngine engine) {
		return engine.isDetectionProviderActive(AvailableDetectionEngines.DP_ID_VD);
	}
}
