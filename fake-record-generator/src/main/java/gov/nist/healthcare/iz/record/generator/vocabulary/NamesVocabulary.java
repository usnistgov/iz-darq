package gov.nist.healthcare.iz.record.generator.vocabulary;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class NamesVocabulary extends Vocabulary {
    List<String> females;
    List<String> males;

    public NamesVocabulary() throws IOException {
        super(Collections.singleton("NAME"));
        this.females = readValues("/vocabulary/female_names.list.txt");
        this.males = readValues("/vocabulary/male_names.list.txt");
    }

    List<String> readValues(String resource) throws IOException {
        return IOUtils.readLines(
                NamesVocabulary.class.getResourceAsStream(resource),
                StandardCharsets.UTF_8);
    }

    @Override
    public String getRandomFieldValue(String field) throws Exception {
        if(females.size() == 0 && males.size() == 0) {
            throw new Exception("No value found in vocabulary table");
        } else {
            int gender = RandomUtils.nextInt(0, 2);
            if(gender == 0) {
                return getRandomValueFromList(this.females);
            } else {
                return getRandomValueFromList(this.males);
            }
        }
    }

    @Override
    public String getRandomRelatedFieldValue(String field, String relatedTo, String relatedToValue) throws Exception {
        if(relatedTo.equals("GENDER") && relatedToValue.equals("M")) {
            return this.getRandomValueFromList(this.males);
        } else if(relatedTo.equals("GENDER") && relatedToValue.equals("F")) {
            return this.getRandomValueFromList(this.females);
        } else {
            return this.getRandomValue(field);
        }
    }

    public String getRandomValueFromList(List<String> names) throws Exception {
        if(names.size() == 0) {
            throw new Exception("No value found in vocabulary table");
        }
        int at = RandomUtils.nextInt(0, names.size());
        return names.get(at);
    }

}
