package gov.nist.healthcare.iz.darq.analyzer.service.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisQuery.Action;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray.*;
import gov.nist.healthcare.iz.darq.analyzer.service.TrayProcessor;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;
import gov.nist.healthcare.iz.darq.digest.domain.ADPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.domain.VaccinationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Field._CG;

public class VaxTrayProcessor extends TrayProcessor {

	public VaxTrayProcessor(Function<Tray, Action> predicate) {
		super(predicate);
	}
	
	@Override
	public _CG analysisPath() {
		return _CG.V;
	}

	void provider(Map<String, Map<String, ADPayload>> db, Tray t){
		for(String provider : db.keySet()){
			t.start(Field.PROVIDER, provider);
			if(!guard(t)) {
				ageGroup(db.get(provider), t);
			}
		}
		t.remove(Field.PROVIDER);
	}
	
	void ageGroup(Map<String, ADPayload> db, Tray t){
		for(String ageGroup : db.keySet()){
			t.add(Field.AGE_GROUP, ageGroup);
			if(!guard(t)) {
				code(db.get(ageGroup).getVaccinationPayload(), t);
			}
		}
		t.remove(Field.AGE_GROUP);
	}
	
	void code(VaccinationPayload db, Tray t){
		for(String vxCode : db.getVaccinations().keySet()){
			t.add(Field.VACCINE_CODE, vxCode);
			if(!guard(t)) {
				year(db.getVaccinations().get(vxCode), t);
			}
		}
		t.remove(Field.VACCINE_CODE);
	}
	
	void year(Map<String, Map<String, Map<String, Integer>>> db, Tray t){
		for(String year : db.keySet()){
			t.add(Field.VACCINATION_YEAR, year);
			if(!guard(t)){
				gender(db.get(year),t);
			}
		}
		t.remove(Field.VACCINATION_YEAR);
	}
	
	void gender(Map<String, Map<String, Integer>> db, Tray t){
		for(String gender : db.keySet()){
			t.add(Field.GENDER, gender);
			if(!guard(t)){
				source(db.get(gender),t);
			}
		}
		t.remove(Field.GENDER);
	}
	
	void source(Map<String, Integer> db, Tray t){
		for(String source : db.keySet()){
			t.add(Field.EVENT, source);
			if(!guard(t)){
				t.setWeigth(db.get(source));
				t.setCount(db.get(source));
				finalize(t);
			}		
		}
		t.remove(Field.EVENT);
	}

	@Override
	public List<Tray> inner(ADFile file) {
		Tray t = new VaxTray();
		provider(file.getReportingGroupPayload(),t);
		return this.work;
	}

}
