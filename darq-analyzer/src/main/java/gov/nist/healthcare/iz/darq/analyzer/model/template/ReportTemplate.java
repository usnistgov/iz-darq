package gov.nist.healthcare.iz.darq.analyzer.model.template;

import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportTemplate {

    @Id
    String id;
    String name;
    String description;
    List<ReportSection> sections;
    ConfigurationPayload configuration;
    String owner;
    @Transient
    boolean viewOnly;
    boolean published;
    Date lastUpdated;

    public ReportTemplate() {
        this.sections = new ArrayList<>();
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

    public List<ReportSection> getSections() {
        return sections;
    }

    public void setSections(List<ReportSection> sections) {
        this.sections = sections;
    }

    public ConfigurationPayload getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ConfigurationPayload configuration) {
        this.configuration = configuration;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isViewOnly() {
        return viewOnly;
    }

    public void setViewOnly(boolean viewOnly) {
        this.viewOnly = viewOnly;
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
}
