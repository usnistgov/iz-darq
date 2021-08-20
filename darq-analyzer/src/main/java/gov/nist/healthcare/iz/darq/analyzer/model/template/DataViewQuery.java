package gov.nist.healthcare.iz.darq.analyzer.model.template;

import gov.nist.healthcare.iz.darq.digest.domain.Field;

import java.util.List;
import java.util.Set;

public class DataViewQuery {

    protected Field._CG type;
    protected String caption;
    protected boolean paginate;
    protected int rows;
    protected Set<Field> occurrences;
    protected Set<Field> groupBy;
    protected List<DataSelector> selectors;
    protected QueryResultFilter filter;
    protected QueryThreshold threshold;

    public Field._CG getType() {
        return type;
    }

    public void setType(Field._CG type) {
        this.type = type;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public boolean isPaginate() {
        return paginate;
    }

    public void setPaginate(boolean paginate) {
        this.paginate = paginate;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public List<DataSelector> getSelectors() {
        return selectors;
    }

    public void setSelectors(List<DataSelector> selectors) {
        this.selectors = selectors;
    }


    public QueryResultFilter getFilter() {
        return filter;
    }

    public void setFilter(QueryResultFilter filter) {
        this.filter = filter;
    }

    public QueryThreshold getThreshold() {
        return threshold;
    }

    public void setThreshold(QueryThreshold threshold) {
        this.threshold = threshold;
    }

    public Set<Field> getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(Set<Field> occurrences) {
        this.occurrences = occurrences;
    }

    public void setGroupBy(Set<Field> groupBy) {
        this.groupBy = groupBy;
    }

    public Set<Field> getGroupBy() {
        return groupBy;
    }
}
