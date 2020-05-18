package gov.nist.healthcare.iz.darq.analyzer.model.template;

import gov.nist.healthcare.iz.darq.digest.domain.Field;

import java.util.Map;
import java.util.Objects;

public class ComplexThreshold {
    Map<Field, ValueContainer> values;
    Threshold goal;

    public Map<Field, ValueContainer> getValues() {
        return values;
    }

    public void setValues(Map<Field, ValueContainer> values) {
        this.values = values;
    }

    public Threshold getGoal() {
        return goal;
    }

    public void setGoal(Threshold goal) {
        this.goal = goal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplexThreshold that = (ComplexThreshold) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
