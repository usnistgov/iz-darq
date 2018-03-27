package gov.nist.healthcare.iz.darq.analyzer.service.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisQuery.Action;
import gov.nist.healthcare.iz.darq.analyzer.domain.Field._CG;
import gov.nist.healthcare.iz.darq.analyzer.domain.Field;
import gov.nist.healthcare.iz.darq.analyzer.domain.Tray;
import gov.nist.healthcare.iz.darq.analyzer.domain.Tray.*;
import gov.nist.healthcare.iz.darq.analyzer.service.TrayProcessor;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;
import gov.nist.healthcare.iz.darq.digest.domain.VaccinationPayload;

public class VaxTrayProcessor extends TrayProcessor {

	public VaxTrayProcessor(Function<Tray, Action> predicate) {
		super(predicate);
	}
	
	@Override
	public _CG analysisPath() {
		return _CG.V;
	}

	void provider(Map<String, Map<String, VaccinationPayload>> db, Tray t){
		for(String provider : db.keySet()){
			t.start(Field.PROVIDER, provider);
			if(!guard(t)) {
				ageGroup(db.get(provider), t);
			}
		}
	}
	
	void ageGroup(Map<String, VaccinationPayload> db, Tray t){
		for(String ageGroup : db.keySet()){
			t.add(Field.AGE_GROUP, ageGroup);
			if(!guard(t)) {
				code(db.get(ageGroup), t);
			}
		}
	}
	
	void code(VaccinationPayload db, Tray t){
		for(String vxCode : db.getVaccinations().keySet()){
			t.add(Field.VACCINE_CODE, vxCode);
			if(!guard(t)) {
				year(db.getVaccinations().get(vxCode), t);
			}
		}
	}
	
	void year(Map<String, Map<String, Map<String, Integer>>> db, Tray t){
		for(String year : db.keySet()){
			t.add(Field.VACCINATION_YEAR, year);
			if(!guard(t)){
				gender(db.get(year),t);
			}
		}
	}
	
	void gender(Map<String, Map<String, Integer>> db, Tray t){
		for(String gender : db.keySet()){
			t.add(Field.GENDER, gender);
			if(!guard(t)){
				source(db.get(gender),t);
			}
		}
	}
	
	void source(Map<String, Integer> db, Tray t){
		for(String source : db.keySet()){
			t.add(Field.EVENT, source);
			if(!guard(t)){
				t.setCount(db.get(source));
				finalize(t);
			}		
		}
	}

	@Override
	public List<Tray> inner(ADFile file) {
		Tray t = new VaxTray();
		provider(file.getVaccinations(),t);
		return this.work;
	}

}
