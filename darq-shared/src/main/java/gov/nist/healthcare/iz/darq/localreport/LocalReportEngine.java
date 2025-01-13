package gov.nist.healthcare.iz.darq.localreport;

import gov.nist.healthcare.iz.darq.detections.DetectionEngine;
import gov.nist.healthcare.iz.darq.detections.RecordDetectionEngineResult;
import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalReportEngine {
	Map<String, LocalReportService> localReports = new HashMap<>();
	List<LocalReportService> activeLocalReportServices = new ArrayList<>();

	public LocalReportEngine(Map<String, LocalReportService> localReportServices) {
		this.localReports.putAll(localReportServices);
	}

	public void configure(LocalReportEngineConfiguration configuration, DetectionEngine detectionEngine) throws Exception {
		for(String localReportId: configuration.getActiveReportEngines()) {
			if(localReports.containsKey(localReportId)) {
				LocalReportService localReportService = localReports.get(localReportId);
				if(localReportService.include(configuration, detectionEngine)) {
					localReportService.open(
							Paths.get(configuration.getTemporaryDirectory()),
							Paths.get(configuration.getOutputDirectory())
					);
					activeLocalReportServices.add(localReportService);
				}
			}
		}
	}

	public void close() throws Exception {
		for(LocalReportService localReportService : activeLocalReportServices) {
			localReportService.close();
		}
	}

	public void process(PreProcessRecord record, RecordDetectionEngineResult recordDetectionEngineResult) throws Exception {
		for(LocalReportService localReportService : activeLocalReportServices) {
			localReportService.process(record, recordDetectionEngineResult);
		}
	}
}
