package gov.nist.healthcare.iz.darq.analyzer.service.bson.tray;

import java.util.*;
import java.util.function.Function;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisQuery.Action;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray.*;
import gov.nist.healthcare.iz.darq.analyzer.service.bson.tray.helper.CodeProcessorHelper;
import gov.nist.healthcare.iz.darq.adf.module.json.model.ADFile;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.domain.PatientPayload;
import gov.nist.healthcare.iz.darq.digest.domain.AnalysisType;

public class PatCodeTrayProcessor extends TrayProcessor {
	CodeProcessorHelper codeProcessorHelper;

	public PatCodeTrayProcessor(Function<Tray, Action> predicate) {
		super(predicate);
		codeProcessorHelper = new CodeProcessorHelper(this::guard, this::finalize);
	}

	@Override
	public List<Tray> inner(ADFile file) {
		ageGroup(file.getGeneralPatientPayload(), new PatCodeTray());
		return this.work;
	}

	void ageGroup(Map<String, PatientPayload> db, Tray t){
		if(db != null) {
			for(String ageGroup : db.keySet()){
				t.start(Field.AGE_GROUP, ageGroup);
				if(!guard(t)) {
					codeProcessorHelper.aggregate(Field.TABLE, Field.CODE, db.get(ageGroup).getCodeTable(), t);
				}
			}
			t.remove(Field.AGE_GROUP);
		}
	}
	
	@Override
	public AnalysisType analysisPath() {
		return AnalysisType.PT;
	}

}
