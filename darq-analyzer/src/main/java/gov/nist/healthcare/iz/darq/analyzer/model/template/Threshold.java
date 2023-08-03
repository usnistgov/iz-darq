package gov.nist.healthcare.iz.darq.analyzer.model.template;

import java.util.Objects;

public class Threshold {
    Comparator comparator;
    double value;

    public Comparator getComparator() {
        return comparator;
    }

    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Threshold threshold = (Threshold) o;
        return Double.compare(threshold.value, value) == 0 && comparator == threshold.comparator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(comparator, value);
    }
}
