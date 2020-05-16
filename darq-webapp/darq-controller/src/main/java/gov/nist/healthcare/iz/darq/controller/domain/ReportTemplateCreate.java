package gov.nist.healthcare.iz.darq.controller.domain;

public class ReportTemplateCreate {
    String name;
    String configurationId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(String configurationId) {
        this.configurationId = configurationId;
    }
}
