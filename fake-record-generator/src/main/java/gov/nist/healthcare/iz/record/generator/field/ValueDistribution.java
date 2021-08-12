package gov.nist.healthcare.iz.record.generator.field;

import java.util.Map;

public class ValueDistribution {
    int valuePresent;
    int valueNotPresent;
    ValueLength valueLength;
    Map<String, Integer> values;

    public int sum() {
        int vl = valueLength != null ? valueLength.getPercent() : 0;
        int values = this.values.values().stream().mapToInt(Integer::intValue).sum();
        return valueNotPresent + valueNotPresent + vl + values;
    }

    public boolean saturated() {
        return sum() == 100;
    }

    public boolean isValid() {
        return sum() <= 100 && sum() >= 0;
    }

    public int getValuePresent() {
        return valuePresent;
    }

    public void setValuePresent(int valuePresent) {
        this.valuePresent = valuePresent;
    }

    public int getValueNotPresent() {
        return valueNotPresent;
    }

    public void setValueNotPresent(int valueNotPresent) {
        this.valueNotPresent = valueNotPresent;
    }

    public ValueLength getValueLength() {
        return valueLength;
    }

    public void setValueLength(ValueLength valueLength) {
        this.valueLength = valueLength;
    }

    public Map<String, Integer> getValues() {
        return values;
    }

    public void setValues(Map<String, Integer> values) {
        this.values = values;
    }
}
