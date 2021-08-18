package gov.nist.healthcare.iz.darq.analyzer.service.tray;

import java.util.List;
import java.util.function.Function;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisQuery.Action;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray.*;
import gov.nist.healthcare.iz.darq.analyzer.service.TrayProcessor;
import gov.nist.healthcare.iz.darq.analyzer.service.tray.helper.CodeProcessorHelper;
import gov.nist.healthcare.iz.darq.analyzer.service.tray.helper.ReportingGroupProcessorHelper;
import gov.nist.healthcare.iz.darq.digest.domain.*;
import gov.nist.healthcare.iz.darq.digest.domain.Field._CG;

public class VaxCodeTrayProcessor extends TrayProcessor {
	CodeProcessorHelper codeProcessorHelper;
	ReportingGroupProcessorHelper reportingGroupProcessorHelper;

	public VaxCodeTrayProcessor(Function<Tray, Action> predicate) {
		super(predicate);
		codeProcessorHelper = new CodeProcessorHelper(this::guard, this::finalize);
		reportingGroupProcessorHelper = new ReportingGroupProcessorHelper(this::guard, this::finalize, this::afterReportingGroupAndAgeGroup);
	}

	@Override
	public List<Tray> inner(ADFile file) {
		reportingGroupProcessorHelper.provider(file.getReportingGroupPayload(), new VaxCodeTray());
		return this.work;
	}

	void afterReportingGroupAndAgeGroup(ADPayload payload, Tray t) {
		if(payload.getVaccinationPayload() != null) {
			codeProcessorHelper.aggregate(Field.TABLE, Field.CODE, payload.getVaccinationPayload().getCodeTable(), t);
		}
	}
	
	@Override
	public _CG analysisPath() {
		return _CG.VT;
	}

}
