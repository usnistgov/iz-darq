package gov.nist.healthcare.iz.darq.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import gov.nist.healthcare.iz.darq.analyzer.model.variable.ExternalQueryVariableScope;
import gov.nist.healthcare.iz.darq.analyzer.model.variable.QueryVariableType;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "scope")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GlobalExternalQueryVariable.class, name = "GLOBAL"),
        @JsonSubTypes.Type(value = IISExternalQueryVariable.class, name = "IIS")
})
public abstract class ExternalQueryVariable extends QueryVariable {
    private ExternalQueryVariableScope scope;
    private Date dateCreated;
    private Set<String> tags;
    private Date dateUpdated;

    public ExternalQueryVariable(ExternalQueryVariableScope scope) {
        super(QueryVariableType.EXTERNAL);
        this.scope = scope;
    }

    public ExternalQueryVariable() {
        super(QueryVariableType.EXTERNAL);
    }

    public ExternalQueryVariableScope getScope() {
        return scope;
    }

    public void setScope(ExternalQueryVariableScope scope) {
        this.scope = scope;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Set<String> getTags() {
        if(tags == null) {
            tags = new HashSet<>();
        }
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
}
