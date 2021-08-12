package gov.nist.healthcare.iz.record.generator.field.generator;

import gov.nist.healthcare.iz.record.generator.RecordType;
import gov.nist.healthcare.iz.record.generator.field.FixedField;
import gov.nist.healthcare.iz.record.generator.model.PatientRecord;

import java.util.Map;

public class FixedFieldGenerator extends FieldGenerator {
    private final FixedField configuration;

    public FixedFieldGenerator(FixedField configuration) {
        this.configuration = configuration;
    }

    @Override
    public String generate(PatientRecord record, RecordType type, String key, Map<String, String> current) {
        return this.configuration.getValue();
    }
}
