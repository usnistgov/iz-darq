package gov.nist.healthcare.iz.darq.analyzer.service.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisQuery.Action;
import gov.nist.healthcare.iz.darq.analyzer.domain.Tray;
import gov.nist.healthcare.iz.darq.analyzer.domain.Tray.*;
import gov.nist.healthcare.iz.darq.analyzer.service.TrayProcessor;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.domain.VaccinationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Field._CG;

public class VaxDetectionTrayProcessor extends TrayProcessor {

	public VaxDetectionTrayProcessor(Function<Tray, Action> predicate) {
		super(predicate);
	}

	@Override
	public List<Tray> inner(ADFile file) {
		provider(file.getVaccinations(), new VaxDetectionTray());
		return this.work;
	}

	void provider(Map<String, Map<String, VaccinationPayload>> db, Tray t){
		for(String provider : db.keySet()){
			t.start(Field.PROVIDER, provider);
			if(!guard(t)) {
				ageGroup(db.get(provider), t);
			}
		}
		t.remove(Field.PROVIDER);
	}
	
	void ageGroup(Map<String, VaccinationPayload> db, Tray t){
		for(String ageGroup : db.keySet()){
			t.add(Field.AGE_GROUP, ageGroup);
			if(!guard(t)) {
				code(db.get(ageGroup), t);
			}	
		}
		t.remove(Field.AGE_GROUP);
	}
	
	void code(VaccinationPayload db, Tray t){
		for(String detCode : db.getDetection().keySet()){
			t.add(Field.DETECTION, detCode);
			if(!guard(t)) {
				t.setWeigth(db.getDetection().get(detCode).getNegative()+db.getDetection().get(detCode).getPositive());
				t.setCount(db.getDetection().get(detCode).getNegative());
				finalize(t);
			}
		}
		t.remove(Field.DETECTION);
	}
	
	@Override
	public _CG analysisPath() {
		return _CG.VD;
	}

}
