package gov.nist.healthcare.iz.record.generator.field.generator;

import gov.nist.healthcare.iz.record.generator.RecordType;
import gov.nist.healthcare.iz.record.generator.field.VocabularyField;
import gov.nist.healthcare.iz.record.generator.model.PatientRecord;
import gov.nist.healthcare.iz.record.generator.vocabulary.Vocabulary;

import java.util.Map;

public class VocabularyFieldGenerator extends FieldGenerator {
    private final VocabularyField configuration;
    private final Vocabulary vocabulary;

    public VocabularyFieldGenerator(VocabularyField configuration) {
        this.configuration = configuration;
        this.vocabulary = configuration.getTable().getVocabulary();
    }

    @Override
    public String generate(PatientRecord record, RecordType type, String key, Map<String, String> current) throws Exception {
        return vocabulary.getRandomValue(configuration.getField());
    }
}
