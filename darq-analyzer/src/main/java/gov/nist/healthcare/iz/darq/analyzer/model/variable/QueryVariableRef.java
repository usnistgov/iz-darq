package gov.nist.healthcare.iz.darq.analyzer.model.variable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "queryValueType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DynamicQueryVariableRef.class, name = "DYNAMIC"),
        @JsonSubTypes.Type(value = QueryVariableRefInstance.class, name = "STATIC")
})
public abstract class QueryVariableRef {
    private String id;
    private QueryVariableType type;
    private ExternalQueryVariableScope scope;
    private QueryValueType queryValueType;

    public QueryVariableRef(String id, QueryVariableType type, ExternalQueryVariableScope scope, QueryValueType queryValueType) {
        this.id = id;
        this.type = type;
        this.scope = scope;
        this.queryValueType = queryValueType;
    }

    public QueryVariableRef(QueryValueType queryValueType) {
        this.queryValueType = queryValueType;
    }

    public QueryValueType getQueryValueType() {
        return queryValueType;
    }

    public void setQueryValueType(QueryValueType queryValueType) {
        this.queryValueType = queryValueType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public QueryVariableType getType() {
        return type;
    }

    public void setType(QueryVariableType type) {
        this.type = type;
    }

    public ExternalQueryVariableScope getScope() {
        return scope;
    }

    public void setScope(ExternalQueryVariableScope scope) {
        this.scope = scope;
    }
}
