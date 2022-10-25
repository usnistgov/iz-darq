package gov.nist.healthcare.iz.darq.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import gov.nist.healthcare.iz.darq.analyzer.model.variable.QueryVariableType;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExternalQueryVariable.class, name = "EXTERNAL"),
        @JsonSubTypes.Type(value = ADFQueryVariable.class, name = "ADF")
})
public abstract class QueryVariable {
    protected String id;
    protected String name;
    protected String description;
    protected QueryVariableType type;

    public QueryVariable() {
    }

    public QueryVariable(String id, String name, String description, QueryVariableType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public QueryVariable(QueryVariableType type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public QueryVariableType getType() {
        return type;
    }

    public void setType(QueryVariableType type) {
        this.type = type;
    }
}
