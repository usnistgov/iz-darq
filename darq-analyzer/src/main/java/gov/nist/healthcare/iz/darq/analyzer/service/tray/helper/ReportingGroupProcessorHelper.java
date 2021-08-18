package gov.nist.healthcare.iz.darq.analyzer.service.tray.helper;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray;
import gov.nist.healthcare.iz.darq.digest.domain.ADPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Field;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ReportingGroupProcessorHelper extends ProcessorHelper {
    protected BiConsumer<ADPayload, Tray> then;

    public ReportingGroupProcessorHelper(Predicate<Tray> guard, Consumer<Tray> finalize, BiConsumer<ADPayload, Tray> then) {
        super(guard, finalize);
        this.then = then;
    }

    public void provider(Map<String, Map<String, ADPayload>> db, Tray t){
        if(db != null) {
            for(String provider : db.keySet()){
                t.start(Field.PROVIDER, provider);
                if(!guard.test(t)) {
                    ageGroup(db.get(provider), t);
                }
            }
            t.remove(Field.PROVIDER);
        }
    }

    public void ageGroup(Map<String, ADPayload> db, Tray t){
        if(db != null) {
            for(String ageGroup : db.keySet()){
                t.add(Field.AGE_GROUP, ageGroup);
                if(!guard.test(t) && db.get(ageGroup) != null) {
                    then.accept(db.get(ageGroup), t);
                }
            }
            t.remove(Field.AGE_GROUP);
        }
    }
}
