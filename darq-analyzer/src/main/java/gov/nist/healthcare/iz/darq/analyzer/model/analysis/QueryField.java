package gov.nist.healthcare.iz.darq.analyzer.model.analysis;

import gov.nist.healthcare.iz.darq.digest.domain.Field;
import java.util.List;

public class QueryField implements Comparable<QueryField>{
    private Field f;
    private List<String> values;
    private boolean all;

    public QueryField(Field f, List<String> values) {
        super();
        this.f = f;
        this.values = values;
    }
    public QueryField(Field f) {
        super();
        this.f = f;
        this.all = true;
    }

    public Field getField() {
        return f;
    }
    public void setField(Field f) {
        this.f = f;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public boolean isAll() {
        return all;
    }
    public void setAll(boolean all) {
        this.all = all;
    }



    @Override
    public String toString() {
        return "QueryField [f=" + f + ", value=" + values + ", all=" + all + "]";
    }
    @Override
    public int compareTo(QueryField o) {
        return all && o.all ? 0 : all && !o.all ? 1 : !all && o.all ? -1 : 0;
    }
}
