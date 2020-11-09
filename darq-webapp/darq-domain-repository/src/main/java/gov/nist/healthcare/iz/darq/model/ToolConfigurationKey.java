package gov.nist.healthcare.iz.darq.model;

public class ToolConfigurationKey {
    private String key;
    private boolean required;

    public ToolConfigurationKey(String key, boolean required) {
        this.key = key;
        this.required = required;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
