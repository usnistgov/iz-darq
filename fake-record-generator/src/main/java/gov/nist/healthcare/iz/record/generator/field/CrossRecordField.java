package gov.nist.healthcare.iz.record.generator.field;

public class CrossRecordField extends Field {
    String field;
    public CrossRecordField() {
        super(FieldType.CROSS);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
