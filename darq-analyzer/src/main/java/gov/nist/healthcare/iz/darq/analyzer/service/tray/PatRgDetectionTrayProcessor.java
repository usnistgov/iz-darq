package gov.nist.healthcare.iz.darq.analyzer.service.tray;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisQuery;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray;
import gov.nist.healthcare.iz.darq.analyzer.service.TrayProcessor;
import gov.nist.healthcare.iz.darq.analyzer.service.tray.helper.DetectionProcessorHelper;
import gov.nist.healthcare.iz.darq.analyzer.service.tray.helper.ReportingGroupProcessorHelper;
import gov.nist.healthcare.iz.darq.digest.domain.*;

import java.util.List;
import java.util.function.Function;

public class PatRgDetectionTrayProcessor extends TrayProcessor {
    DetectionProcessorHelper detectionProcessorHelper;
    ReportingGroupProcessorHelper reportingGroupProcessorHelper;

    public PatRgDetectionTrayProcessor(Function<Tray, AnalysisQuery.Action> predicate) {
        super(predicate);
        detectionProcessorHelper = new DetectionProcessorHelper(this::guard, this::finalize);
        reportingGroupProcessorHelper = new ReportingGroupProcessorHelper(this::guard, this::finalize, this::afterReportingGroupAndAgeGroup);
    }

    @Override
    public Field._CG analysisPath() {
        return Field._CG.PD_RG;
    }

    @Override
    public List<Tray> inner(ADFile file) {
        reportingGroupProcessorHelper.provider(file.getReportingGroupPayload(), new Tray.PatRgDetectionTray());
        return this.work;
    }

    void afterReportingGroupAndAgeGroup(ADPayload payload, Tray t) {
        if(payload.getPatientPayload() != null) {
            detectionProcessorHelper.detection(payload.getPatientPayload().getDetection(), t);
        }
    }
}
