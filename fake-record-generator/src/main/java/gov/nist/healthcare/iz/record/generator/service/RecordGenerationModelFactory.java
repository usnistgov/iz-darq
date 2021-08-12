package gov.nist.healthcare.iz.record.generator.service;

import gov.nist.healthcare.iz.record.generator.RecordType;
import gov.nist.healthcare.iz.record.generator.field.*;
import gov.nist.healthcare.iz.record.generator.field.generator.*;
import gov.nist.healthcare.iz.record.generator.model.Configuration;
import gov.nist.healthcare.iz.record.generator.model.RecordGenerationModel;
import org.joda.time.LocalDate;

import java.util.Map;

public class RecordGenerationModelFactory {

    public static RecordGenerationModel createRecordGenerationModel(RecordType type, Configuration configuration, Map<String, Field> fields) throws Exception {
        RecordGenerationModel recordGenerationModel = new RecordGenerationModel();
        recordGenerationModel.setRecordType(type);
        while (recordGenerationModel.getFieldsGenerationOrder().size() != fields.keySet().size()) {
            fields.entrySet().stream().filter((field) -> {
                boolean notHandled = !recordGenerationModel.getFieldsGenerationOrder().contains(field.getKey());
                boolean hasDependencyHandled = recordGenerationModel.getFieldsGenerationOrder().containsAll(field.getValue().getDependencies());
                return notHandled && hasDependencyHandled;
            }).forEach((field) -> recordGenerationModel.getFieldsGenerationOrder().add(field.getKey()));
        }

        for(String field : recordGenerationModel.getFieldsGenerationOrder()) {
            recordGenerationModel.getGenerators().put(field, createFieldGenerator(field, type, fields.get(field), configuration, fields));
        }

        return recordGenerationModel;
    }

    public static FieldGenerator createFieldGenerator(String key, RecordType recordType, Field field, Configuration configuration, Map<String, Field> fields) throws Exception {
        int total = getTotal(recordType, configuration);
        if(!field.allParamsAreMapped()) {
            throw new Exception("Field "+ key +" has unmapped dependencies");
        }
        switch (field.getType()) {
            case RANDOM : {
                return new RandomFieldGenerator((RandomField) field);
            }
            case FIXED : {
                return new FixedFieldGenerator((FixedField) field);
            }
            case VOCABULARY : {
                return new VocabularyFieldGenerator((VocabularyField) field);
            }
            case CODED: {
                CodeField codeField = (CodeField) field;
                return new CodeFieldGenerator(codeField, (CodedField) fields.get(codeField.getParamsMapTo().get("LINK_TO")));
            }
            case DOB : {
                return new DateOfBirthFieldGenerator((DateOfBirthField) field, total, new LocalDate(configuration.getAsOf()));
            }
            case NAME: {
                return new NameFieldGenerator((NameField) field);
            }
            case STATE_AWARE: {
                return new StateAwareFieldGenerator((StateAwareField) field);
            }
            case ENUM: {
                return new EnumFieldGenerator((EnumField) field, configuration.getEnums());
            }
            case VALUE_DISTRIBUTION: {
                return new ValueDistributionFieldGenerator(
                        (ValueDistributionField) field,
                        configuration,
                        total,
                        key,
                        recordType,
                        fields
                );
            }
            case MAPPED:
                return new MappedFieldGenerator(
                        (MappedField) field,
                        key,
                        recordType,
                        fields,
                        configuration
                );
            case DATE_OP:
                return new DateOpFieldGenerator(
                        (DateOpField) field
                );
            case DATE_BETWEEN:
                return new DateBetweenGenerator(
                        (DateBetweenField) field
                );
            case COPY:
                return new CopyFieldGenerator(
                        (CopyField) field
                );
            case CROSS:
                if(recordType.equals(RecordType.PATIENT)) {
                    throw new Exception("Cannot cross a field to patient record type");
                }
                return new CrossRecordFieldGenerator(
                        (CrossRecordField) field
                );
        }

        return null;
    }

    public static int getTotal(RecordType recordType, Configuration configuration) {
        if(recordType.equals(RecordType.PATIENT)) {
            return configuration.getNumberOfPatients();
        } else {
            return configuration.getNumberOfVaccinations();
        }
    }

}
