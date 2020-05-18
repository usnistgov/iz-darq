package gov.nist.healthcare.iz.darq.analyzer.model.template;

import java.util.Objects;

public class ValueContainer {

    private String value;

    public ValueContainer(String value) {
        this.value = value;
    }

    public ValueContainer() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueContainer that = (ValueContainer) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
