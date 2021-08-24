package gov.nist.healthcare.iz.darq.analyzer.model.template;

import gov.nist.healthcare.iz.darq.digest.domain.Field;

import java.util.List;
import java.util.Set;

public class DataViewQuery extends QueryPayload {
    protected Set<Field> occurrences;
    protected Set<Field> groupBy;
    protected Set<DataSelector> selectors;
    protected QueryThreshold threshold;

    public DataViewQuery() {
        super(QueryPayloadType.ADVANCED);
    }

    @Override
    public Set<Field> getNominatorFields() {
        return this.getOccurrences();
    }

    @Override
    public Set<Field> getDenominatorFields() {
        return this.getGroupBy();
    }

    @Override
    public Set<DataSelector> getFilterFields() {
        return this.getSelectors();
    }

    @Override
    public QueryThreshold getQueryThreshold() {
        return this.getThreshold();
    }

    public Set<Field> getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(Set<Field> occurrences) {
        this.occurrences = occurrences;
    }

    public Set<Field> getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(Set<Field> groupBy) {
        this.groupBy = groupBy;
    }

    public Set<DataSelector> getSelectors() {
        return selectors;
    }

    public void setSelectors(Set<DataSelector> selectors) {
        this.selectors = selectors;
    }

    public void setThreshold(QueryThreshold threshold) {
        this.threshold = threshold;
    }

    public QueryThreshold getThreshold() {
        return threshold;
    }
}
