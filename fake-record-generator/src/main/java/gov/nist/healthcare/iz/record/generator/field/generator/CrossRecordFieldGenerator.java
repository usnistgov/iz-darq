package gov.nist.healthcare.iz.record.generator.field.generator;

import gov.nist.healthcare.iz.record.generator.RecordType;
import gov.nist.healthcare.iz.record.generator.field.CrossRecordField;
import gov.nist.healthcare.iz.record.generator.model.PatientRecord;

import java.util.Map;

public class CrossRecordFieldGenerator extends FieldGenerator {
    CrossRecordField configuration;

    public CrossRecordFieldGenerator(CrossRecordField configuration) {
        this.configuration = configuration;
    }

    @Override
    public String generate(PatientRecord record, RecordType type, String key, Map<String, String> current) throws Exception {
        return record.getPatient().get(configuration.getField());
    }
}
