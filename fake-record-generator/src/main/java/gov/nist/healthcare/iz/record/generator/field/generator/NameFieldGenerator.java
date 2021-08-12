package gov.nist.healthcare.iz.record.generator.field.generator;

import gov.nist.healthcare.iz.record.generator.RecordType;
import gov.nist.healthcare.iz.record.generator.field.NameField;
import gov.nist.healthcare.iz.record.generator.model.PatientRecord;
import gov.nist.healthcare.iz.record.generator.vocabulary.VocabularyTable;

import java.util.Map;

public class NameFieldGenerator extends FieldGenerator {
    private final NameField configuration;

    public NameFieldGenerator(NameField configuration) {
        this.configuration = configuration;
    }

    @Override
    public String generate(PatientRecord record, RecordType type, String key, Map<String, String> current) throws Exception {
        VocabularyTable TABLE = configuration.isFamily() ? VocabularyTable.LAST_NAME : VocabularyTable.FIRST_NAME;
        Map<String, String> params = configuration.getParamValues(current);
        if(params.containsKey("GENDER")) {
            return TABLE.getVocabulary().getRandomRelatedValue("NAME", "GENDER", params.get("GENDER"));
        } else {
            return TABLE.getVocabulary().getRandomValue("NAME");
        }
    }
}
