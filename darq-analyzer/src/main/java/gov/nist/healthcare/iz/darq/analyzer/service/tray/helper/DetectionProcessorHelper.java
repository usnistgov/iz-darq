package gov.nist.healthcare.iz.darq.analyzer.service.tray.helper;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray;
import gov.nist.healthcare.iz.darq.digest.domain.DetectionSum;
import gov.nist.healthcare.iz.darq.digest.domain.Field;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DetectionProcessorHelper extends ProcessorHelper {
    public DetectionProcessorHelper(Predicate<Tray> guard, Consumer<Tray> finalize) {
        super(guard, finalize);
    }

    public void detection(Map<String, DetectionSum> db, Tray t) {
        if(db != null) {
            for (String detCode : db.keySet()) {
                t.add(Field.DETECTION, detCode);
                if (!guard.test(t)) {
                    t.setWeigth(db.get(detCode).getNegative() + db.get(detCode).getPositive());
                    t.setCount(db.get(detCode).getNegative());
                    finalize.accept(t);
                }
            }
            t.remove(Field.DETECTION);
        }
    }
}
