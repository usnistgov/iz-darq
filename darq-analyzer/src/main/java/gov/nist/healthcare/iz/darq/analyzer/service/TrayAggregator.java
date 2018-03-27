package gov.nist.healthcare.iz.darq.analyzer.service;

import java.util.List;

import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisPayload;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisSectionResult;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisSectionResult.AnalysisPayloadResult;
import gov.nist.healthcare.iz.darq.analyzer.domain.ReportSection;
import gov.nist.healthcare.iz.darq.analyzer.domain.Tray;

public interface TrayAggregator {

	AnalysisPayloadResult aggregate(List<Tray> trays, AnalysisPayload payload);

}
