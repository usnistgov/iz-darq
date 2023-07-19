package gov.nist.healthcare.iz.darq.analyzer.service.bson.tray.helper;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.domain.TablePayload;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CodeProcessorHelper extends ProcessorHelper {
    public CodeProcessorHelper(Predicate<Tray> guard, Consumer<Tray> finalize) {
        super(guard, finalize);
    }

    public void aggregate(Field TABLE, Field CODE, Map<String, TablePayload> db, Tray t){
        if(db != null) {
            for(String table : db.keySet()){
                t.add(TABLE, table);
                if(!guard.test(t)) {
                    TablePayload payload = db.get(table);
                    code(payload, t, CODE);
                }
            }
            t.remove(TABLE);
        }
    }

    public void code(TablePayload db, Tray t, Field CODE){
        if(db != null) {
            for(String code : db.getCodes().keySet()){
                t.add(CODE, code);
                if(!guard.test(t)) {
                    t.setWeigth(db.getCodes().get(code));
                    t.setCount(db.getCodes().get(code));
                    finalize.accept(t);
                }
            }
            t.remove(CODE);
        }
    }
}
