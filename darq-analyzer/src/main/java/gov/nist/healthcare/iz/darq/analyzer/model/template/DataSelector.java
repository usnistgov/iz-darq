package gov.nist.healthcare.iz.darq.analyzer.model.template;

import gov.nist.healthcare.iz.darq.digest.domain.Field;

import java.util.List;

public class DataSelector {
    private Field field;
    private List<ValueContainer> values;

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public List<ValueContainer> getValues() {
        return values;
    }

    public void setValues(List<ValueContainer> values) {
        this.values = values;
    }
}
