package gov.nist.healthcare.iz.darq.analyzer.service.tray;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisQuery.Action;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray.*;
import gov.nist.healthcare.iz.darq.analyzer.service.TrayProcessor;
import gov.nist.healthcare.iz.darq.analyzer.service.tray.helper.CodeProcessorHelper;
import gov.nist.healthcare.iz.darq.analyzer.service.tray.helper.DetectionProcessorHelper;
import gov.nist.healthcare.iz.darq.analyzer.service.tray.helper.ReportingGroupProcessorHelper;
import gov.nist.healthcare.iz.darq.digest.domain.*;
import gov.nist.healthcare.iz.darq.digest.domain.Field._CG;

public class VaxDetectionTrayProcessor extends TrayProcessor {
	DetectionProcessorHelper detectionProcessorHelper;
	ReportingGroupProcessorHelper reportingGroupProcessorHelper;

	public VaxDetectionTrayProcessor(Function<Tray, Action> predicate) {
		super(predicate);
		detectionProcessorHelper = new DetectionProcessorHelper(this::guard, this::finalize);
		reportingGroupProcessorHelper = new ReportingGroupProcessorHelper(this::guard, this::finalize, this::afterReportingGroupAndAgeGroup);
	}

	@Override
	public _CG analysisPath() {
		return _CG.VD;
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
