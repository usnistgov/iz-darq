package gov.nist.healthcare.iz.darq.analyzer.model.template;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import java.util.Set;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "payloadType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DataViewQuery.class, name = "ADVANCED"),
        @JsonSubTypes.Type(value = SimpleViewQuery.class, name = "SIMPLE")
})
public abstract class QueryPayload {
    private final QueryPayloadType payloadType;
    protected Field._CG type;
    protected String caption;
    protected boolean paginate;
    protected int rows;
    protected QueryResultFilter filter;

    public QueryPayload(QueryPayloadType payloadType) {
        this.payloadType = payloadType;
    }

    @JsonIgnore
    public abstract Set<Field> getNominatorFields();
    @JsonIgnore
    public abstract Set<Field> getDenominatorFields();
    @JsonIgnore
    public abstract Set<DataSelector> getFilterFields();
    @JsonIgnore
    public abstract QueryThreshold getQueryThreshold();

    public QueryPayloadType getPayloadType() {
        return payloadType;
    }

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

    public QueryResultFilter getFilter() {
        return filter;
    }

    public void setFilter(QueryResultFilter filter) {
        this.filter = filter;
    }


}
