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
import gov.nist.healthcare.iz.darq.digest.domain.PatientPayload;

public class PatDetectionTrayProcessor extends TrayProcessor {

	public PatDetectionTrayProcessor(Function<Tray, Action> predicate) {
		super(predicate);
	}

	@Override
	public List<Tray> inner(ADFile file) {
		ageGroup(file.getPatients(), new PatDetectionTray());
		return this.work;
	}

	void ageGroup(Map<String, PatientPayload> db, Tray t) {
		for (String ageGroup : db.keySet()) {
			t.add(Field.AGE_GROUP, ageGroup);
			if (!guard(t)) {
				code(db.get(ageGroup), t);
			}

		}
	}

	void code(PatientPayload db, Tray t) {
		for (String detCode : db.getDetection().keySet()) {
			t.add(Field.DETECTION, detCode);
			if (!guard(t)) {
				t.setWeigth(db.getDetection().get(detCode).getNegative() + db.getDetection().get(detCode).getPositive());
				t.setCount(db.getDetection().get(detCode).getNegative());
				finalize(t);
			}

		}
	}

	@Override
	public _CG analysisPath() {
		return _CG.PD;
	}
}