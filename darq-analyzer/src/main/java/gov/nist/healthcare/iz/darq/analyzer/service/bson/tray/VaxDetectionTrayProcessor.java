package gov.nist.healthcare.iz.darq.analyzer.service.bson.tray;

import java.util.List;
import java.util.function.Function;

import gov.nist.healthcare.iz.darq.adf.module.json.model.ADFile;
import gov.nist.healthcare.iz.darq.analyzer.service.bson.tray.helper.DetectionProcessorHelper;
import gov.nist.healthcare.iz.darq.analyzer.service.bson.tray.helper.ReportingGroupProcessorHelper;
import gov.nist.healthcare.iz.darq.digest.domain.ADPayload;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisQuery.Action;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray.*;
import gov.nist.healthcare.iz.darq.digest.domain.AnalysisType;

public class VaxDetectionTrayProcessor extends TrayProcessor {
	DetectionProcessorHelper detectionProcessorHelper;
	ReportingGroupProcessorHelper reportingGroupProcessorHelper;

	public VaxDetectionTrayProcessor(Function<Tray, Action> predicate) {
		super(predicate);
		detectionProcessorHelper = new DetectionProcessorHelper(this::guard, this::finalize);
		reportingGroupProcessorHelper = new ReportingGroupProcessorHelper(this::guard, this::finalize, this::afterReportingGroupAndAgeGroup);
	}

	@Override
	public AnalysisType analysisPath() {
		return AnalysisType.VD;
	}

	@Override
	public List<Tray> inner(ADFile file) {
		reportingGroupProcessorHelper.provider(file.getReportingGroupPayload(), new VaxDetectionTray());
		return this.work;
	}

	void afterReportingGroupAndAgeGroup(ADPayload payload, Tray t) {
		if(payload.getVaccinationPayload() != null) {
			detectionProcessorHelper.detection(payload.getVaccinationPayload().getDetection(), t);
		}
	}

}
