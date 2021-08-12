package gov.nist.healthcare.iz.record.generator.distribution;

import gov.nist.healthcare.iz.record.generator.field.generator.FieldGenerator;

public class GeneratorDistribution {
    int weight;
    FieldGenerator generator;

    public GeneratorDistribution(int weight, FieldGenerator generator) {
        this.weight = weight;
        this.generator = generator;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public FieldGenerator getGenerator() {
        return generator;
    }

    public void setGenerator(FieldGenerator generator) {
        this.generator = generator;
    }
}
