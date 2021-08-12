package gov.nist.healthcare.iz.record.generator.field.generator;

import gov.nist.healthcare.iz.record.generator.RecordType;
import gov.nist.healthcare.iz.record.generator.field.ValueLengthField;
import gov.nist.healthcare.iz.record.generator.model.PatientRecord;
import org.apache.commons.lang3.RandomUtils;

import java.util.Map;
import java.util.Set;

public class ValueLengthFieldGenerator extends FieldGenerator {
    private final ValueLengthField configuration;

    public ValueLengthFieldGenerator(ValueLengthField configuration) {
        this.configuration = configuration;
    }

    @Override
    public String generate(PatientRecord record, RecordType type, String key, Map<String, String> current) throws Exception {
        int len = RandomUtils.nextInt(configuration.getMin(), configuration.getMax() + 1);
        return "[[LEN{"+len+"}]]";
    }

    @Override
    public void exclude(Set<String> values) throws Exception {
        throw new Exception("Exclusion is not supported for "+ this.getClass().getName());
    }
}
