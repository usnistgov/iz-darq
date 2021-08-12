package gov.nist.healthcare.iz.record.generator.vocabulary;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public enum VocabularyTable {
    FIRST_NAME, LAST_NAME, STATE_ZIP_AREA, CITY;

    static {
        try {
            ObjectMapper mapper = new ObjectMapper();

            FIRST_NAME.setVocabulary(new NamesVocabulary());
            LAST_NAME.setVocabulary(new ValueSetVocabulary(IOUtils.readLines(
                    VocabularyTable.class.getResourceAsStream("/vocabulary/last_name.list.txt"),
                    StandardCharsets.UTF_8), "NAME"));
            STATE_ZIP_AREA.setVocabulary(new StateRelatedVocabulary());
            TypeReference<List<String>> typeRef
                    = new TypeReference<List<String>>() {};
            CITY.setVocabulary(new ValueSetVocabulary(
                    mapper.readValue(VocabularyTable.class.getResourceAsStream("/vocabulary/city_names.json"), typeRef),
                    "CITY"
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Vocabulary table;

    private void setVocabulary(Vocabulary table) {
        this.table = table;
    }

    public Vocabulary getVocabulary() {
        return this.table;
    }


    VocabularyTable() {
    }
}
