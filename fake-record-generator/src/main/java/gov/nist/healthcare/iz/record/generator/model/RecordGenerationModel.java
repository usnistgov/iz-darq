package gov.nist.healthcare.iz.record.generator.model;

import gov.nist.healthcare.iz.record.generator.RecordType;
import gov.nist.healthcare.iz.record.generator.field.generator.FieldGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordGenerationModel {
    private RecordType recordType;
    private Map<String, FieldGenerator> generators = new HashMap<>();
    private List<String> fieldsGenerationOrder = new ArrayList<>();

    public RecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(RecordType recordType) {
        this.recordType = recordType;
    }

    public Map<String, FieldGenerator> getGenerators() {
        return generators;
    }

    public void setGenerators(Map<String, FieldGenerator> generators) {
        this.generators = generators;
    }

    public List<String> getFieldsGenerationOrder() {
        return fieldsGenerationOrder;
    }

    public void setFieldsGenerationOrder(List<String> fieldsGenerationOrder) {
        this.fieldsGenerationOrder = fieldsGenerationOrder;
    }
}
