package gov.nist.healthcare.iz.record.generator.field.generator;

import gov.nist.healthcare.iz.record.generator.RecordType;
import gov.nist.healthcare.iz.record.generator.field.StateAwareField;
import gov.nist.healthcare.iz.record.generator.model.PatientRecord;
import gov.nist.healthcare.iz.record.generator.vocabulary.Vocabulary;
import gov.nist.healthcare.iz.record.generator.vocabulary.VocabularyTable;

import java.util.Map;

public class StateAwareFieldGenerator extends FieldGenerator {
    private final StateAwareField configuration;
    private final Vocabulary vocabulary;

    public StateAwareFieldGenerator(StateAwareField configuration) {
        this.configuration = configuration;
        this.vocabulary = VocabularyTable.STATE_ZIP_AREA.getVocabulary();
    }

    @Override
    public String generate(PatientRecord record, RecordType type, String key, Map<String, String> current) throws Exception {
        Map<String, String> params = configuration.getParamValues(current);
        if(params.containsKey("STATE")) {
            return vocabulary.getRandomRelatedValue(configuration.getField().name(), "STATE", params.get("STATE"));
        } else {
            return vocabulary.getRandomValue(configuration.getField().name());
        }
    }
}