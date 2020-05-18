package gov.nist.healthcare.iz.darq.analyzer.model.template;

import gov.nist.healthcare.iz.darq.digest.domain.Field;

import java.util.List;
import java.util.Map;

public class GroupFilter extends Filter {
    boolean keep;
    List<Map<Field, ValueContainer>> values;

    public boolean isKeep() {
        return keep;
    }

    public void setKeep(boolean keep) {
        this.keep = keep;
    }

    public List<Map<Field, ValueContainer>> getValues() {
        return values;
    }

    public void setValues(List<Map<Field, ValueContainer>> values) {
        this.values = values;
    }
}