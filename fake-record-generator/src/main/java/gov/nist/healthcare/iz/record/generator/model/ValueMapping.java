package gov.nist.healthcare.iz.record.generator.model;

import gov.nist.healthcare.iz.record.generator.field.Field;

import java.util.Map;

public class ValueMapping {
    String from;
    String to;
    Map<String, Field> mapping;

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

    public Map<String, Field> getMapping() {
        return mapping;
    }

    public void setMapping(Map<String, Field> mapping) {
        this.mapping = mapping;
    }
}
