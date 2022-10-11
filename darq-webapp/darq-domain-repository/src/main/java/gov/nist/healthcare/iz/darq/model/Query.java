package gov.nist.healthcare.iz.darq.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nist.healthcare.domain.trait.Owned;
import gov.nist.healthcare.iz.darq.analyzer.model.template.QueryPayload;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;

import java.util.Date;

public class Query implements Owned {

    private String id;
    private String name;
    @Deprecated
    private String owner;
    private String ownerId;
    private Date lastUpdated;
    private Date dateCreated;
    private ConfigurationPayload configuration;
    private String description;
    private QueryPayload query;

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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public ConfigurationPayload getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ConfigurationPayload configuration) {
        this.configuration = configuration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public QueryPayload getQuery() {
        return query;
    }

    public void setQuery(QueryPayload query) {
        this.query = query;
    }

    @Override
    @JsonProperty("owner")
    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

}
