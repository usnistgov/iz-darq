package gov.nist.healthcare.iz.darq.model;

public class ToolConfigurationKeyValue {
    private String key;
    private String value;

    public ToolConfigurationKeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public ToolConfigurationKeyValue() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
