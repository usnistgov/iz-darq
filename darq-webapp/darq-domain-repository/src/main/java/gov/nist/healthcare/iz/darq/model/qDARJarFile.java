package gov.nist.healthcare.iz.darq.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.nio.file.Path;

public class qDARJarFile {
    private Path location;
    private String version;
    private String mqeVersion;
    private String buildAt;
    private String keyHash;
    private String description;

    public qDARJarFile() {
    }

    public qDARJarFile(Path location, String version, String mqeVersion, String buildAt, String keyHash, String description) {
        this.location = location;
        this.version = version;
        this.mqeVersion = mqeVersion;
        this.buildAt = buildAt;
        this.keyHash = keyHash;
        this.description = description;
    }

    @JsonIgnore
    public Path getLocation() {
        return location;
    }

    public void setLocation(Path location) {
        this.location = location;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMqeVersion() {
        return mqeVersion;
    }

    public void setMqeVersion(String mqeVersion) {
        this.mqeVersion = mqeVersion;
    }

    public String getBuildAt() {
        return buildAt;
    }

    public void setBuildAt(String buildAt) {
        this.buildAt = buildAt;
    }

    public String getKeyHash() {
        return keyHash;
    }

    public void setKeyHash(String keyHash) {
        this.keyHash = keyHash;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
