package gov.nist.healthcare.iz.record.generator.field.generator;

import gov.nist.healthcare.iz.record.generator.RecordType;
import gov.nist.healthcare.iz.record.generator.distribution.GeneratorDistribution;
import gov.nist.healthcare.iz.record.generator.field.*;
import gov.nist.healthcare.iz.record.generator.model.Configuration;
import gov.nist.healthcare.iz.record.generator.model.PatientRecord;
import gov.nist.healthcare.iz.record.generator.service.RecordGenerationModelFactory;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ValueDistributionFieldGenerator extends FieldGenerator {
    ValueDistributionField configuration;
    private final List<GeneratorDistribution> generators;

    public ValueDistributionFieldGenerator(
            ValueDistributionField configuration,
            Configuration general,
            int total,
            String key,
            RecordType recordType,
            Map<String, Field> fields
    ) throws Exception {
        this.configuration = configuration;
        if(!configuration.getDistribution().isValid()) {
            throw new Exception("Invalid distribution " + configuration.getDistribution().sum());
        }
        generators = new ArrayList<>();
        ValueDistribution distribution = configuration.getDistribution();

        // ValuePresent
        int vp = getWeight(distribution.getValuePresent(), total);
        if(vp != 0) {
            generators.add(makeFixedValueGenerator(vp, "[[VP]]"));
        }

        // ValueNotPresent
        int np = getWeight(distribution.getValuePresent(), total);
        if(np != 0) {
            generators.add(makeFixedValueGenerator(vp, "[[NP]]"));
        }

        // ValueLength
        if(distribution.getValueLength() != null) {
            int vl = getWeight(distribution.getValueLength().getPercent(), total);
            if(np != 0) {
                generators.add(makeValueLengthGenerator(
                        vl,
                        distribution.getValueLength().getMin(),
                        distribution.getValueLength().getMax()
                ));
            }
        }

        // Values
        if(distribution.getValues() != null) {
            for(String value: distribution.getValues().keySet()) {
                int v = getWeight(distribution.getValues().get(value), total);
                if(v != 0) {
                    generators.add(makeFixedValueGenerator(v, value));
                }
            }
        }

        if(!distribution.saturated()) {
            int weightLeft = total - getTotalWeight();
            FieldGenerator generator = RecordGenerationModelFactory.createFieldGenerator(
                    key,
                    recordType,
                    configuration.getGenerator(),
                    general,
                    fields
            );
            assert generator != null;
            generator.exclude(distribution.getValues() != null ? distribution.getValues().keySet() : null);
            generators.add(
                    new GeneratorDistribution(
                            weightLeft,
                            generator
                    )
            );
        }
    }

    public GeneratorDistribution makeFixedValueGenerator(int weight, String value) {
        FixedField fixedField = new FixedField();
        fixedField.setValue(value);
        return new GeneratorDistribution(weight, new FixedFieldGenerator(
                fixedField
        ));
    }

    public GeneratorDistribution makeValueLengthGenerator(int weight, int min, int max) {
        ValueLengthField field = new ValueLengthField();
        field.setMax(max);
        field.setMin(min);
        return new GeneratorDistribution(weight, new ValueLengthFieldGenerator(
                field
        ));
    }

    public int getWeight(int value, int total) throws Exception {
        if(value < 0 || value > 100) {
            throw new Exception("Invalid distribution value " + configuration.getDistribution().sum());
        }
        return (value * total) / 100;
    }

    public int getTotalWeight() {
        return generators.stream().mapToInt(GeneratorDistribution::getWeight).sum();
    }

    public FieldGenerator draw(int value) throws Exception {
        int min = 0;
        for(GeneratorDistribution gd: generators) {
            int current = value - min;
            if(current >= 0 && current < gd.getWeight()) {
                gd.setWeight(gd.getWeight() - 1);
                return gd.getGenerator();
            }
            min += gd.getWeight();
        }
        throw new Exception("Could not draw value");
    }

    @Override
    public String generate(PatientRecord record, RecordType type, String key, Map<String, String> current) throws Exception {
        int pick = RandomUtils.nextInt(0, getTotalWeight());
        FieldGenerator generator = draw(pick);
        return generator.getRandomAcceptableValue(record, type, key, current);
    }
}
