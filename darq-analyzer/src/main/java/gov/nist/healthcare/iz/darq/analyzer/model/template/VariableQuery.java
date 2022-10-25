package gov.nist.healthcare.iz.darq.analyzer.model.template;

import gov.nist.healthcare.iz.darq.digest.domain.Field;

import java.util.HashSet;
import java.util.Set;

public class VariableQuery extends QueryPayload {
    private GlobalThreshold threshold;

    public VariableQuery() {
        super(QueryPayloadType.VARIABLE);
    }

    @Override
    public Set<Field> getNominatorFields() {
        return new HashSet<>();
    }

    @Override
    public Set<Field> getDenominatorFields() {
        return new HashSet<>();
    }

    @Override
    public Set<DataSelector> getFilterFields() {
        return new HashSet<>();
    }

    @Override
    public QueryThreshold getQueryThreshold() {
        QueryThreshold queryThreshold = new QueryThreshold();
        queryThreshold.setCustom(new CustomThreshold());
        queryThreshold.setGlobal(this.threshold);
        return queryThreshold;
    }

    public GlobalThreshold getThreshold() {
        return threshold;
    }

    public void setThreshold(GlobalThreshold threshold) {
        this.threshold = threshold;
    }

}
