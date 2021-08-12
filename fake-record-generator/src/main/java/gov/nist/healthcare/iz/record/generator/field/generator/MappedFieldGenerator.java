package gov.nist.healthcare.iz.record.generator.field.generator;

import gov.nist.healthcare.iz.record.generator.RecordType;
import gov.nist.healthcare.iz.record.generator.field.Field;
import gov.nist.healthcare.iz.record.generator.field.MappedField;
import gov.nist.healthcare.iz.record.generator.model.Configuration;
import gov.nist.healthcare.iz.record.generator.model.PatientRecord;
import gov.nist.healthcare.iz.record.generator.model.ValueMapping;
import gov.nist.healthcare.iz.record.generator.service.RecordGenerationModelFactory;

import java.util.HashMap;
import java.util.Map;

public class MappedFieldGenerator extends FieldGenerator {
    MappedField configuration;
    Map<String, FieldGenerator> mapping;
    FieldGenerator generator;
    public MappedFieldGenerator(MappedField configuration, String key, RecordType recordType, Map<String, Field> fields, Configuration general) throws Exception {
        this.configuration = configuration;
        if(general.getValueMappings() != null) {
            Map<String, Field> valueMap = general.getValueMappings().stream().filter((mapping) ->
                    mapping.getFrom().equals(configuration.getFrom()) &&
                    mapping.getTo().equals(configuration.getTo())
            ).findAny().map(ValueMapping::getMapping).orElse(null);

            if(valueMap == null) {
                throw new Exception("No mapping from " + configuration.getFrom() + " to " + configuration.getTo() + " defined");
            }

            Map<String, FieldGenerator> result = new HashMap<>();
            for(String value: valueMap.keySet()) {
                result.put(value, RecordGenerationModelFactory.createFieldGenerator(
                        key,
                        recordType,
                        valueMap.get(value),
                        general,
                        fields
                ));
            }
            this.mapping = result;
        }

        if(configuration.getGenerator() != null) {
            this.generator = RecordGenerationModelFactory.createFieldGenerator(
                    key,
                    recordType,
                    configuration.getGenerator(),
                    general,
                    fields
            );
        } else {
            throw new Exception("No default value defined");
        }
    }

    @Override
    public String generate(PatientRecord record, RecordType type, String key, Map<String, String> current) throws Exception {
        Map<String, String> params = configuration.getParamValues(current);
        if(!params.containsKey("FROM")) {
            throw new Exception("Mappings required FROM param not found");
        } else {
            if(mapping.containsKey(params.get("FROM"))) {
                return mapping.get(params.get("FROM")).generate(record, type, key, current);
            } else {
                return generator.generate(record, type, key, current);
            }
        }
    }
}
//    "VX_GROUP" : { "transient": true,  "type" :  "CODED", "codeSet" :  "VACCINE_GROUP", "paramsMapTo": { "LINK_TO" :  "CVX", "AS" : "VACCINATION_CVX_CODE" }},
////    "VX_GROUP_CVX" : { "transient" :  true, "type" :  "CODED", "codeSet" :  "VACCINATION_CVX_CODE", "paramsMapTo": { "LINK_TO" :  "VX_GROUP", "AS" : "VACCINE_GROUP" }},