package gov.nist.healthcare.iz.record.generator.field;

import gov.nist.healthcare.iz.record.generator.field.generator.FieldGenerator;

import java.util.HashMap;

public class MappedField extends Field {
    private String from;
    private String to;
    private Field generator;

    public MappedField() {
        super(FieldType.MAPPED);
        this.setParams(new HashMap<String, Boolean>() {{
            put("FROM", true);
        }});
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Field getGenerator() {
        return generator;
    }

    public void setGenerator(Field generator) {
        this.generator = generator;
    }
}
