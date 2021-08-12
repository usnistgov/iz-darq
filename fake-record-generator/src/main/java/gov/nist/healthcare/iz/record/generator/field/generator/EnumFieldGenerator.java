package gov.nist.healthcare.iz.record.generator.field.generator;

import gov.nist.healthcare.iz.record.generator.RecordType;
import gov.nist.healthcare.iz.record.generator.field.EnumField;
import gov.nist.healthcare.iz.record.generator.model.PatientRecord;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnumFieldGenerator extends FieldGenerator {
    EnumField configuration;
    List<String> values;

    public EnumFieldGenerator(EnumField configuration, Map<String, Set<String>> enums) throws Exception {
        this.configuration = configuration;
        if(enums == null || !enums.containsKey(configuration.getName()) || enums.get(configuration.getName()) == null || enums.get(configuration.getName()).size() == 0) {
            throw new Exception("Enum " + configuration.getName() + " not found");
        }
        this.values = new ArrayList<>(enums.get(configuration.getName()));
    }

    @Override
    public String generate(PatientRecord record, RecordType type, String key, Map<String, String> current) throws Exception {
        int at = RandomUtils.nextInt(0, values.size());
        return values.get(at);
    }
}
