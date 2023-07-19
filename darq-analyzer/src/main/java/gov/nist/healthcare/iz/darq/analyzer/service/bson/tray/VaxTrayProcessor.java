package gov.nist.healthcare.iz.darq.analyzer.service.bson.tray;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import gov.nist.healthcare.iz.darq.adf.module.json.model.ADFile;
import gov.nist.healthcare.iz.darq.digest.domain.ADPayload;
import gov.nist.healthcare.iz.darq.digest.domain.TablePayload;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisQuery.Action;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray.*;
import gov.nist.healthcare.iz.darq.analyzer.service.bson.tray.helper.CodeProcessorHelper;
import gov.nist.healthcare.iz.darq.analyzer.service.bson.tray.helper.ReportingGroupProcessorHelper;
import gov.nist.healthcare.iz.darq.digest.domain.*;
import gov.nist.healthcare.iz.darq.digest.domain.AnalysisType;

public class VaxTrayProcessor extends TrayProcessor {
	CodeProcessorHelper codeProcessorHelper;
	ReportingGroupProcessorHelper reportingGroupProcessorHelper;

	public VaxTrayProcessor(Function<Tray, Action> predicate) {
		super(predicate);
		codeProcessorHelper = new CodeProcessorHelper(this::guard, this::finalize);
		reportingGroupProcessorHelper = new ReportingGroupProcessorHelper(this::guard, this::finalize, this::afterReportingGroupAndAgeGroup);
	}

	@Override
	public AnalysisType analysisPath() {
		return AnalysisType.V;
	}

	@Override
	public List<Tray> inner(ADFile file) {
		Tray t = new VaxTray();
		reportingGroupProcessorHelper.provider(file.getReportingGroupPayload(),t);
		return this.work;
	}


	void afterReportingGroupAndAgeGroup(ADPayload payload, Tray t) {
		if(payload.getVaccinationPayload() != null) {
			year(payload.getVaccinationPayload().getVaccinations(), t);
		}
	}

	void year(Map<String, Map<String, Map<String, TablePayload>>> db, Tray t){
		if(db != null) {
			for(String year : db.keySet()){
				t.add(Field.VACCINATION_YEAR, year);
				if(!guard(t)){
					gender(db.get(year),t);
				}
			}
			t.remove(Field.VACCINATION_YEAR);
		}
	}

	void gender(Map<String, Map<String, TablePayload>> db, Tray t){
		if(db != null) {
			for(String gender : db.keySet()){
				t.add(Field.GENDER, gender);
				if(!guard(t)){
					codeProcessorHelper.aggregate(Field.EVENT, Field.VACCINE_CODE, db.get(gender), t);
				}
			}
			t.remove(Field.GENDER);
		}
	}

}
