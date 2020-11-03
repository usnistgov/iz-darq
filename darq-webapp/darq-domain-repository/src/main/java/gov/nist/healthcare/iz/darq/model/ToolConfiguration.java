package gov.nist.healthcare.iz.darq.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document()
public class ToolConfiguration {
    public static final String TOOL_CONFIGURATION_ID = "TOOL_CONFIGURATION";

    @Id
    private String id;
    private Set<ToolConfigurationProperty> properties;

    public void setProperties(Set<ToolConfigurationProperty> properties) {
        this.properties = properties;
    }

    public Set<ToolConfigurationProperty> getProperties() {
        if(properties == null) {
            this.setProperties(new HashSet<>());
        }
        return properties;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
