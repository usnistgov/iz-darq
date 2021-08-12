package gov.nist.healthcare.iz.record.generator.vocabulary;

import java.util.Set;

public abstract class Vocabulary {
    Set<String> fields;

    public Vocabulary(Set<String> fields) {
        this.fields = fields;
    }

    Set<String> getFields() {
        return fields;
    }

    public String getRandomValue(String field) throws Exception {
        if(this.getFields().contains(field)) {
            return this.getRandomFieldValue(field);
        } else {
            throw new Exception("Unrecognized field " + field);
        }
    }
    protected abstract String getRandomFieldValue(String field) throws Exception;
    public String getRandomRelatedValue(String field, String relatedTo, String relatedToValue) throws Exception {
        if(this.getFields().contains(field)) {
            return this.getRandomRelatedFieldValue(field, relatedTo, relatedToValue);
        } else {
            throw new Exception("Unrecognized field " + field);
        }
    }
    abstract String getRandomRelatedFieldValue(String field, String relatedTo, String relatedToValue) throws Exception;
}
