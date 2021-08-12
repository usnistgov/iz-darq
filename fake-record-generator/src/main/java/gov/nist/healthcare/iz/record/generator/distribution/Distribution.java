package gov.nist.healthcare.iz.record.generator.distribution;
import com.google.common.base.Strings;
import org.apache.commons.lang3.RandomUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Distribution {
    Map<String, DistributionValue> distribution;
    List<String> possibleValues;
    private final int numberOfRecords;

    public Distribution(
            Set<String> values,
            boolean enumeration,
            Map<String, Integer> distribution,
            int numberOfRecords,
            String dummy
    ) throws Exception {
        this.numberOfRecords = numberOfRecords;
        this.distribution = new HashMap<>();
        if(distribution != null) {
            for(String value: distribution.keySet()) {
                if(enumeration && !values.contains(value)) {
                    throw new Exception("Value "+ value +" is not allowed for distribution");
                }
                this.put(value, distribution.get(value));
            }
            boolean saturated = this.getDistributionSum() == 1;
            Set<String> possibleValues = new HashSet<>(this.distribution.keySet());
            if(!saturated) {
                Set<String> additional = values.stream().filter((pv) -> !this.distribution.containsKey(pv)).collect(Collectors.toSet());
                if(additional.size() == 0 && Strings.isNullOrEmpty(dummy)) {
                    throw new Exception("Distribution is not exhaustive ");
                } else {
                    possibleValues.addAll(additional);
                    possibleValues.add(dummy);
                }
            }
            this.possibleValues = new ArrayList<>(possibleValues);
        } else {
            this.possibleValues = new ArrayList<>(values);
        }
    }

    public Distribution(
            Set<String> values,
            Map<String, Integer> distribution,
            int numberOfRecords
    ) throws Exception {
        this(values, true, distribution, numberOfRecords, null);
    }

    public Distribution(
            Map<String, Integer> distribution,
            int numberOfRecords,
            String dummy
    ) throws Exception {
        this(null, false, distribution, numberOfRecords, dummy);
    }

    private double getDistributionSum() {
        return distribution.values().stream().map(DistributionValue::getGoal).mapToDouble(Double::doubleValue).sum();
    }

    private void put(String value, int percentage) throws Exception {
        final double ratio = percentage / 100.0;
        if(percentage > 100 || percentage < 0) {
            throw new Exception("Distribution of "+ percentage +" is not allowed");
        }
        double total = distribution.entrySet().stream().map((entry) -> entry.getKey().equals(value) ? ratio : entry.getValue().getGoal()).mapToDouble(Double::doubleValue).sum();
        if(total > 1) {
            throw new Exception("Value "+ value +" with percentage of "+ percentage +" will overflow (total " + total*100 + "%)");
        } else {
            distribution.put(value, new DistributionValue(percentage / 100.0, this.numberOfRecords));
        }
    }

    public String getRandomValue() throws Exception {
        if(this.possibleValues.size() == 0) {
            throw new Exception("Exhausted all values");
        }
        int rnd = RandomUtils.nextInt(0, this.possibleValues.size());
        String value = this.possibleValues.get(rnd);
        if(distribution.containsKey(value)) {
            DistributionValue distributionValue = distribution.get(value);
            distributionValue.plusOne();
            if(distributionValue.isSatisfied()) {
                this.possibleValues.remove(value);
            }
        }
        return value;
    }
}
