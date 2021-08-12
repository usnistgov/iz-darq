package gov.nist.healthcare.iz.record.generator.field;

import java.util.List;
import java.util.Map;

public class DateOfBirthField extends Field {
    private List<Integer> ageGroups;
    private Map<Integer, Integer> distribution;
    private int overflow;

    public DateOfBirthField() {
        super(FieldType.DOB);
    }

    public List<Integer> getAgeGroups() {
        return ageGroups;
    }

    public void setAgeGroups(List<Integer> ageGroups) {
        this.ageGroups = ageGroups;
    }

    public Map<Integer, Integer> getDistribution() {
        return distribution;
    }

    public void setDistribution(Map<Integer, Integer> distribution) {
        this.distribution = distribution;
    }

    public int getOverflow() {
        return overflow;
    }

    public void setOverflow(int overflow) {
        this.overflow = overflow;
    }
}
