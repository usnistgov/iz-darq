package gov.nist.healthcare.iz.record.generator.field.generator;

import gov.nist.healthcare.iz.record.generator.RecordType;
import gov.nist.healthcare.iz.record.generator.model.PatientRecord;

import java.util.Map;
import java.util.Set;

public abstract class FieldGenerator {
    public Set<String> exclusions;
    private final int RETRIES = 3;

    public void exclude(Set<String> values) throws Exception {
        this.exclusions = values;
    }
    public abstract String generate(PatientRecord record, RecordType type, String key, Map<String, String> current) throws Exception;
    public String getRandomAcceptableValue(PatientRecord record, RecordType type, String key, Map<String, String> current) throws Exception {
        String value = "";
        int count = 0;
        do {
            value = generate(record, type, key, current);
            count++;
        } while (isExcluded(value) && count <= RETRIES);

        if(isExcluded(value)) {
            throw new Exception("Was unable to generate unexcluded value in " + RETRIES + " retries" + this.getClass().getName());
        }
        return value;
    }
    public String populate(PatientRecord record, RecordType type, String key, Map<String, String> current) throws Exception {
        return current.put(key, getRandomAcceptableValue(record, type, key, current));
    }

    public boolean isExcluded(String value) {
        return exclusions != null && exclusions.contains(value);
    }
}
