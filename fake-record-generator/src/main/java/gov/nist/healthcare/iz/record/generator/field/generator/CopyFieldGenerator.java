package gov.nist.healthcare.iz.record.generator.field.generator;

import gov.nist.healthcare.iz.record.generator.RecordType;
import gov.nist.healthcare.iz.record.generator.field.CopyField;
import gov.nist.healthcare.iz.record.generator.model.PatientRecord;

import java.util.Map;

public class CopyFieldGenerator extends FieldGenerator {
    private final CopyField configuration;

    public CopyFieldGenerator(CopyField configuration) {
        this.configuration = configuration;
    }

    @Override
    public String generate(PatientRecord record, RecordType type, String key, Map<String, String> current) throws Exception {
        Map<String, String> params = configuration.getParamValues(current);
        return params.get("COPY_OF");
    }
}
