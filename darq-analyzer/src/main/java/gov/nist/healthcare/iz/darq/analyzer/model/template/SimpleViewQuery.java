package gov.nist.healthcare.iz.darq.analyzer.model.template;

import gov.nist.healthcare.iz.darq.digest.domain.Field;

import java.util.Collections;
import java.util.Set;

public class SimpleViewQuery extends QueryPayload {
    private DataSingleSelector filterBy;
    private Field nominator;
    private SimpleQueryDenominator denominator;
    private GlobalThreshold threshold;

    public SimpleViewQuery() {
        super(QueryPayloadType.SIMPLE);
    }

    @Override
    public Set<Field> getNominatorFields() {
        return nominator != null ? Collections.singleton(nominator) : Collections.emptySet();
    }

    @Override
    public Set<Field> getDenominatorFields() {
        return denominator != null && denominator.isActive() && denominator.getField() != null ? Collections.singleton(denominator.getField()) : Collections.emptySet();
    }

    @Override
    public Set<DataSelector> getFilterFields() {
        if(filterBy != null) {
            DataSelector selector = new DataSelector();
            ValueContainer container = new ValueContainer();
            container.setValue(this.filterBy.getValue());
            selector.setField(this.filterBy.getField());
            selector.setValues(Collections.singletonList(container));
            return Collections.singleton(selector);
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public QueryThreshold getQueryThreshold() {
        QueryThreshold queryThreshold = new QueryThreshold();
        queryThreshold.setCustom(new CustomThreshold());
        queryThreshold.setGlobal(this.threshold);
        return queryThreshold;
    }

    public DataSingleSelector getFilterBy() {
        return filterBy;
    }

    public void setFilterBy(DataSingleSelector filterBy) {
        this.filterBy = filterBy;
    }

    public Field getNominator() {
        return nominator;
    }

    public void setNominator(Field nominator) {
        this.nominator = nominator;
    }

    public SimpleQueryDenominator getDenominator() {
        return denominator;
    }

    public void setDenominator(SimpleQueryDenominator denominator) {
        this.denominator = denominator;
    }

    public GlobalThreshold getThreshold() {
        return threshold;
    }

    public void setThreshold(GlobalThreshold threshold) {
        this.threshold = threshold;
    }
}
