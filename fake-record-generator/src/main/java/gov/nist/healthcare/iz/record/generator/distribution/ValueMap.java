package gov.nist.healthcare.iz.record.generator.distribution;

import gov.nist.healthcare.iz.record.generator.field.ValueDistribution;
import org.apache.commons.lang3.RandomUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ValueMap {
    int total;
    int percent;
    boolean saturated;
    int drafts;
    Map<String, Integer> values;
    List<String> profiledValues;

    public ValueMap(ValueDistribution distribution, Set<String> possible, int total) {
        this.values = new HashMap<>();
        this.total = total;
        this.drafts = 0;
        this.percent = distribution.sum();
        this.saturated = distribution.saturated();
    }

    public boolean focusOnProfiled() {
        int profiled = this.values.values().stream().mapToInt(Integer::intValue).sum();
        return this.total - drafts == profiled;
    }


    public String drawFromValues() throws Exception {
        if(this.profiledValues.size() == 0) {
            throw new Exception("Exhausted all values");
        }
        int at = RandomUtils.nextInt(0, profiledValues.size());
        String drawn = this.profiledValues.get(at);
        int occurrences = this.values.get(drawn);
        if(occurrences == 1) {
            this.values.remove(drawn);
            this.profiledValues.remove(drawn);
        } else {
            this.values.put(drawn, occurrences - 1);
        }
        drafts++;
        return drawn;
    }

}
