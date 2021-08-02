package gov.nist.healthcare.iz.darq.analyzer.model.analysis;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nist.healthcare.domain.trait.Owned;
import gov.nist.healthcare.domain.trait.Publishable;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.Date;
import java.util.Map;

public class Report implements Owned, Publishable {
    @Id
    protected String id;
    protected String name;
    protected String description;
    protected ConfigurationPayload configuration;
    @Deprecated
    protected String owner;
    protected String ownerId;
    protected boolean published;
    protected Date lastUpdated;
    protected Map<String, String> customDetectionLabels;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getCustomDetectionLabels() {
        return customDetectionLabels;
    }

    public void setCustomDetectionLabels(Map<String, String> customDetectionLabels) {
        this.customDetectionLabels = customDetectionLabels;
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

    public ConfigurationPayload getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ConfigurationPayload configuration) {
        this.configuration = configuration;
    }
    @Deprecated
    @JsonIgnore
    public String getOwner() {
        return owner;
    }
    @Deprecated
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    @JsonProperty("owner")
    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    @Transient
    @JsonProperty("public")
    public boolean isPublic() {
        return this.isPublished();
    }
}
