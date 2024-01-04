package gov.nist.healthcare.iz.darq.analyzer.service.bson.tray;

import java.util.List;
import java.util.function.Function;

import gov.nist.healthcare.iz.darq.adf.module.json.model.ADFile;
import gov.nist.healthcare.iz.darq.analyzer.service.bson.tray.helper.CodeProcessorHelper;
import gov.nist.healthcare.iz.darq.analyzer.service.bson.tray.helper.ReportingGroupProcessorHelper;
import gov.nist.healthcare.iz.darq.digest.domain.ADPayload;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisQuery.Action;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray.*;
import gov.nist.healthcare.iz.darq.digest.domain.*;
import gov.nist.healthcare.iz.darq.digest.domain.AnalysisType;

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
	public AnalysisType analysisPath() {
		return AnalysisType.VT;
	}

}
