package gov.nist.healthcare.iz.darq.controller.domain;

import gov.nist.healthcare.iz.darq.analyzer.model.template.QueryPayload;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;

public class QuerySaveRequest {
    private String id;
    private QueryPayload query;
    private ConfigurationPayload configuration;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public QueryPayload getQuery() {
        return query;
    }

    public void setQuery(QueryPayload query) {
        this.query = query;
    }

    public ConfigurationPayload getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ConfigurationPayload configuration) {
        this.configuration = configuration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
