package gov.nist.healthcare.iz.darq.analyzer.service.bson.tray;

import gov.nist.healthcare.iz.darq.adf.module.json.model.ADFile;
import gov.nist.healthcare.iz.darq.analyzer.service.bson.tray.helper.CodeProcessorHelper;
import gov.nist.healthcare.iz.darq.analyzer.service.bson.tray.helper.ReportingGroupProcessorHelper;
import gov.nist.healthcare.iz.darq.digest.domain.ADPayload;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisQuery;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray;
import gov.nist.healthcare.iz.darq.digest.domain.*;

import java.util.*;
import java.util.function.Function;

public class PatRgCodeTrayProcessor extends TrayProcessor {
    CodeProcessorHelper codeProcessorHelper;
    ReportingGroupProcessorHelper reportingGroupProcessorHelper;

    public PatRgCodeTrayProcessor(Function<Tray, AnalysisQuery.Action> predicate) {
        super(predicate);
        codeProcessorHelper = new CodeProcessorHelper(this::guard, this::finalize);
        reportingGroupProcessorHelper = new ReportingGroupProcessorHelper(this::guard, this::finalize, this::afterReportingGroupAndAgeGroup);
    }

    @Override
    public AnalysisType analysisPath() {
        return AnalysisType.PT_RG;
    }

    @Override
    public List<Tray> inner(ADFile file) {
        reportingGroupProcessorHelper.provider(file.getReportingGroupPayload(), new Tray.PatCodeTray());
        return this.work;
    }

    void afterReportingGroupAndAgeGroup(ADPayload payload, Tray t) {
        if(payload.getPatientPayload() != null) {
            codeProcessorHelper.aggregate(Field.TABLE, Field.CODE, payload.getPatientPayload().getCodeTable(), t);
        }
    }
}
