package gov.nist.healthcare.iz.darq.analyzer.model.template;

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
}
