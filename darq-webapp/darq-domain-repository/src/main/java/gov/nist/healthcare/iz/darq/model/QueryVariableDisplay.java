package gov.nist.healthcare.iz.darq.model;

import gov.nist.healthcare.iz.darq.analyzer.model.variable.DynamicQueryVariableRef;
import gov.nist.healthcare.iz.darq.analyzer.model.variable.ExternalQueryVariableScope;
import gov.nist.healthcare.iz.darq.analyzer.model.variable.QueryVariableRefInstance;
import gov.nist.healthcare.iz.darq.analyzer.model.variable.QueryVariableType;


public class QueryVariableDisplay {
    private String id;
    private String name;
    private String description;
    private QueryVariableType type;
    private ExternalQueryVariableScope scope;
    private QueryVariableRefInstance snapshot;
    private DynamicQueryVariableRef dynamic;

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

    public ExternalQueryVariableScope getScope() {
        return scope;
    }

    public void setScope(ExternalQueryVariableScope scope) {
        this.scope = scope;
    }

    public QueryVariableRefInstance getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(QueryVariableRefInstance snapshot) {
        this.snapshot = snapshot;
    }

    public DynamicQueryVariableRef getDynamic() {
        return dynamic;
    }

    public void setDynamic(DynamicQueryVariableRef dynamic) {
        this.dynamic = dynamic;
    }
}
