package gov.nist.healthcare.iz.darq.analyzer.model.template;

import gov.nist.healthcare.iz.darq.digest.domain.Field;

public class SimpleQueryDenominator extends Filter {
    private Field field;

    public SimpleQueryDenominator(Field field) {
        this.field = field;
    }

    public SimpleQueryDenominator() {
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }
}
