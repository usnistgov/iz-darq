package gov.nist.healthcare.iz.darq.analyzer.service.impl;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisQuery;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray;
import gov.nist.healthcare.iz.darq.analyzer.service.TrayProcessor;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;
import gov.nist.healthcare.iz.darq.digest.domain.ADPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.domain.PatientPayload;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PatRgDetectionTrayProcessor extends TrayProcessor {

    public PatRgDetectionTrayProcessor(Function<Tray, AnalysisQuery.Action> predicate) {
        super(predicate);
    }

    @Override
    public List<Tray> inner(ADFile file) {
        provider(file.getReportingGroupPayload(), new Tray.PatRgDetectionTray());
        return this.work;
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

    void ageGroup(Map<String, ADPayload> db, Tray t) {
        for (String ageGroup : db.keySet()) {
            t.start(Field.AGE_GROUP, ageGroup);
            if (!guard(t)) {
                code(db.get(ageGroup).getPatientPayload(), t);
            }
        }
        t.remove(Field.AGE_GROUP);
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
        t.remove(Field.DETECTION);
    }

    @Override
    public Field._CG analysisPath() {
        return Field._CG.PD;
    }
}
