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
import gov.nist.healthcare.iz.darq.digest.domain.TablePayload;
import gov.nist.healthcare.iz.darq.digest.domain.VaccinationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Field._CG;

public class VaxCodeTrayProcessor extends TrayProcessor {

	public VaxCodeTrayProcessor(Function<Tray, Action> predicate) {
		super(predicate);
	}

	@Override
	public List<Tray> inner(ADFile file) {
		provider(file.getVaccinations(), new VaxCodeTray());
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
				table(db.get(ageGroup), t);
			}
		}
		t.remove(Field.AGE_GROUP);
	}
	
	void table(VaccinationPayload db, Tray t){
		for(String table : db.getCodeTable().keySet()){
			t.add(Field.TABLE, table);
			if(!guard(t)) {
				code(db.getCodeTable().get(table), t);
			}	
		}
		t.remove(Field.TABLE);
	}
	
	void code(TablePayload db, Tray t){
		for(String code : db.getCodes().keySet()){
			t.add(Field.CODE, code);
			if(!guard(t)) {
				t.setWeigth(db.getCodes().get(code));
				t.setCount(db.getCodes().get(code));
				finalize(t);
			}
		}
		t.remove(Field.CODE);
	}
	
	@Override
	public _CG analysisPath() {
		return _CG.VT;
	}

}
