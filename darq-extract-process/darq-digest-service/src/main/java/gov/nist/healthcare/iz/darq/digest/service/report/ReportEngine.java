package gov.nist.healthcare.iz.darq.digest.service.report;

import gov.nist.healthcare.iz.darq.detections.AggregatedRecordDetections;
import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportEngine {
	Map<String, LocalReportService> localReports = new HashMap<>();

	public ReportEngine(List<LocalReportService> localReportServices) {
		for (LocalReportService localReportService : localReportServices) {
			localReports.put(localReportService.getFilename(), localReportService);
		}
	}

	public void open() throws Exception {
		for(LocalReportService localReportService : localReports.values()) {
			localReportService.open();
		}
	}

	public void close() throws Exception {
		for(LocalReportService localReportService : localReports.values()) {
			localReportService.close();
		}
	}

	public void process(AggregatePatientRecord record, AggregatedRecordDetections detections) throws Exception {
		for(LocalReportService localReportService : localReports.values()) {
			localReportService.process(record, detections);
		}
	}
}
