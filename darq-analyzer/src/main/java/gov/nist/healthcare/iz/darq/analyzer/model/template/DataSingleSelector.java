package gov.nist.healthcare.iz.darq.analyzer.model.template;

import gov.nist.healthcare.iz.darq.digest.domain.Field;

import java.util.List;

public class DataSingleSelector {
    private Field field;
    private String value;

    public DataSingleSelector(Field field, String value) {
        this.field = field;
        this.value = value;
    }

    public DataSingleSelector() {
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
