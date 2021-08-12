package gov.nist.healthcare.iz.record.generator.vocabulary;
import org.apache.commons.lang3.RandomUtils;

import java.util.*;

public class ValueSetVocabulary extends Vocabulary {
    List<String> values;

    public ValueSetVocabulary(List<String> values, String field) {
        super(Collections.singleton(field));
        this.values = new ArrayList<>(values);
    }

    @Override
    public String getRandomFieldValue(String field) throws Exception {
        if(values.size() == 0) {
            throw new Exception("No value found in vocabulary table");
        }
        int at = RandomUtils.nextInt(0, values.size());
        return values.get(at);
    }

    @Override
    public String getRandomRelatedFieldValue(String field, String relatedTo, String relatedToValue) throws Exception {
        return getRandomFieldValue(field);
    }

}
