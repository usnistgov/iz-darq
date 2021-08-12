package gov.nist.healthcare.iz.record.generator.vocabulary;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapVocabularyTable {
    private String[][] values;
    Map<String, Integer> fields;

    public Set<String> getValueFor(String field, Map<String, String> query) throws Exception {
        Set<String> result = new HashSet<>();
        if(!fields.containsKey(field)) {
            throw new Exception("Unknown field " + field);
        }

        for(int i = 0; i < values.length; i++) {
            int closureI = i;
            if(query.keySet().stream().allMatch((qField) ->
                    values[closureI][fields.get(qField)].equals(query.get(qField))
            )) {
                result.add(values[i][fields.get(field)]);
            }
        }

        return result;
    }
}
