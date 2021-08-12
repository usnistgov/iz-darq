package gov.nist.healthcare.iz.record.generator.field;

public class ValueLengthField extends Field {
    private int min;
    private int max;

    public ValueLengthField() {
        super(FieldType.VALUE_LENGTH);
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
