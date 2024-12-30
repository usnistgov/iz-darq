package gov.nist.healthcare.iz.darq.digest.service.report;

import gov.nist.healthcare.iz.darq.detections.RecordDetectionEngineResult;
import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;

import java.nio.file.Path;
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

	public void open(Path tempDirectory, Path outputDirectory) throws Exception {
		for(LocalReportService localReportService : localReports.values()) {
			localReportService.open(tempDirectory, outputDirectory);
		}
	}

	public void close() throws Exception {
		for(LocalReportService localReportService : localReports.values()) {
			localReportService.close();
		}
	}

	public void process(PreProcessRecord record, RecordDetectionEngineResult recordDetectionEngineResult) throws Exception {
		for(LocalReportService localReportService : localReports.values()) {
			localReportService.process(record, recordDetectionEngineResult);
		}
	}
}
