package gov.nist.healthcare.iz.record.generator.field.generator;

import gov.nist.healthcare.iz.record.generator.RecordType;
import gov.nist.healthcare.iz.record.generator.field.RandomField;
import gov.nist.healthcare.iz.record.generator.model.PatientRecord;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import java.util.Map;

public class RandomFieldGenerator extends FieldGenerator {
    private final RandomField configuration;
    public RandomFieldGenerator(RandomField configuration) {
        this.configuration = configuration;
    }

    @Override
    public String generate(PatientRecord record, RecordType type, String key, Map<String, String> current) {
        String value;
        switch (configuration.getFormat()) {
            case PHONE:
                value = getPhoneStringValue();
                break;
            case EMAIL:
                value = getEmailStringValue();
                break;
            case NUMBER:
                value = getNumberStringValue();
                break;
            case ALPHANUMERIC:
                value = getAlphaNumericStringValue();
                break;
            default:
                value = getAlphabeticStringValue();
        }
        return configuration.isUppercase() ? value.toUpperCase() : value;
    }

    public int getRandomLength() {
        return RandomUtils.nextInt(this.configuration.getMinLength(), this.configuration.getMaxLength());
    }

    public String getAlphaNumericStringValue() {
        return RandomStringUtils.randomAlphanumeric(getRandomLength());
    }

    public String getAlphabeticStringValue() {
        return RandomStringUtils.randomAlphabetic(getRandomLength());
    }

    public String getNumberStringValue() {
        return RandomStringUtils.randomNumeric(getRandomLength());
    }

    public String getPhoneStringValue() {
        int areaCode = RandomUtils.nextInt(201, 989);
        int numberMiddle = RandomUtils.nextInt(100, 999);
        int numberEnd = RandomUtils.nextInt(1000, 9999);
        return "(" + areaCode + ")" + numberMiddle + numberEnd;
    }

    public String getEmailStringValue() {
        String username = RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(4, 10));
        String host = "gmail";
        String dot = "com";
        return username + "@" + host + "." + dot;
    }

}
