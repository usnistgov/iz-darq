package gov.nist.healthcare.iz.darq.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExternalQueryVariable.class, name = "EXTERNAL"),
        @JsonSubTypes.Type(value = ADFQueryVariable.class, name = "ADF")
})
public abstract class QueryVariable {
    private String id;
    private String name;
    private String description;
    private QueryVariableType type;

    public QueryVariable() {
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
