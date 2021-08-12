package gov.nist.healthcare.iz.record.generator.field;

public class FixedField extends Field {
    private String value;

    public FixedField() {
        super(FieldType.FIXED);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
