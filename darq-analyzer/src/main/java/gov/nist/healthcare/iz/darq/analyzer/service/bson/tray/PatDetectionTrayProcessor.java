package gov.nist.healthcare.iz.darq.analyzer.service.bson.tray;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisQuery.Action;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray.*;
import gov.nist.healthcare.iz.darq.analyzer.service.bson.tray.helper.DetectionProcessorHelper;
import gov.nist.healthcare.iz.darq.adf.module.json.model.ADFile;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.domain.PatientPayload;
import gov.nist.healthcare.iz.darq.digest.domain.AnalysisType;

public class PatDetectionTrayProcessor extends TrayProcessor {
	DetectionProcessorHelper detectionProcessorHelper;

	public PatDetectionTrayProcessor(Function<Tray, Action> predicate) {
		super(predicate);
		detectionProcessorHelper = new DetectionProcessorHelper(this::guard, this::finalize);
	}

	@Override
	public AnalysisType analysisPath() {
		return AnalysisType.PD;
	}

	@Override
	public List<Tray> inner(ADFile file) {
		ageGroup(file.getGeneralPatientPayload(), new PatDetectionTray());
		return this.work;
	}

	void ageGroup(Map<String, PatientPayload> db, Tray t) {
		if(db != null) {
			for (String ageGroup : db.keySet()) {
				t.start(Field.AGE_GROUP, ageGroup);
				if (!guard(t)) {
					detectionProcessorHelper.detection(db.get(ageGroup).getDetection(), t);
				}
			}
			t.remove(Field.AGE_GROUP);
		}
	}
}