package gov.nist.healthcare.iz.record.generator.field;

public class ValueDistributionField extends Field {
    private ValueDistribution distribution;
    private Field generator;

    protected ValueDistributionField() {
        super(FieldType.VALUE_DISTRIBUTION);
    }

    public ValueDistribution getDistribution() {
        return distribution;
    }

    public void setDistribution(ValueDistribution distribution) {
        this.distribution = distribution;
    }

    public Field getGenerator() {
        return generator;
    }

    public void setGenerator(Field generator) {
        this.generator = generator;
    }
}
