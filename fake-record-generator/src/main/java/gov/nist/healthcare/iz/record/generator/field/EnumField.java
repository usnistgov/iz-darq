package gov.nist.healthcare.iz.record.generator.field;

public class EnumField extends Field {
    private String name;
    public EnumField() {
        super(FieldType.ENUM);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
