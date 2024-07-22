package gov.nist.healthcare.iz.darq.digest.domain;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class ADFMetaData {
	String id;
	String name;
	String path;
	@Deprecated
	String owner;
	String ownerId;
	Date analysedOn;
	Date uploadedOn;
	String size;
	String keyHash;
	ConfigurationPayload configuration;
	Summary summary;
	String version;
	String build;
	String mqeVersion;
	Set<String> inactiveDetections;
	List<String> tags;
	long totalAnalysisTime;

	public ADFMetaData(
			String name,
			List<String> tags,
			String path,
			String owner,
			String ownerId,
			Date analysedOn,
			Date uploadedOn,
			ConfigurationPayload configuration,
			String keyHash,
			Summary summary,
			String size,
			String version,
			String build,
			String mqeVersion,
			Set<String> inactiveDetections,
			long totalAnalysisTime
	) {
		super();
		this.name = name;
		this.tags = tags;
		this.path = path;
		this.keyHash = keyHash;
		this.owner = owner;
		this.ownerId = ownerId;
		this.analysedOn = analysedOn;
		this.uploadedOn = uploadedOn;
		this.configuration = configuration;
		this.summary = summary;
		this.size = size;
		this.version = version;
		this.build = build;
		this.mqeVersion = mqeVersion;
		this.inactiveDetections = inactiveDetections;
		this.totalAnalysisTime = totalAnalysisTime;
	}
	
	public ADFMetaData() {
		super();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	@Deprecated
	public String getOwner() {
		return owner;
	}
	@Deprecated
	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public Date getAnalysedOn() {
		return analysedOn;
	}
	public void setAnalysedOn(Date analysedOn) {
		this.analysedOn = analysedOn;
	}
	public Date getUploadedOn() {
		return uploadedOn;
	}
	public void setUploadedOn(Date uploadedOn) {
		this.uploadedOn = uploadedOn;
	}
	public ConfigurationPayload getConfiguration() {
		return configuration;
	}
	public void setConfiguration(ConfigurationPayload configuration) {
		this.configuration = configuration;
	}
	public Summary getSummary() {
		return summary;
	}
	public void setSummary(Summary summary) {
		this.summary = summary;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getKeyHash() {
		return keyHash;
	}
	public void setKeyHash(String keyHash) {
		this.keyHash = keyHash;
	}
	public boolean getHasCLIInfo() {
		return this.version != null && !this.version.isEmpty();
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getBuild() {
		return build;
	}

	public void setBuild(String build) {
		this.build = build;
	}

	public String getMqeVersion() {
		return mqeVersion;
	}

	public void setMqeVersion(String mqeVersion) {
		this.mqeVersion = mqeVersion;
	}

	public Set<String> getInactiveDetections() {
		return inactiveDetections;
	}

	public void setInactiveDetections(Set<String> inactiveDetections) {
		this.inactiveDetections = inactiveDetections;
	}

	public long getTotalAnalysisTime() {
		return totalAnalysisTime;
	}

	public void setTotalAnalysisTime(long totalAnalysisTime) {
		this.totalAnalysisTime = totalAnalysisTime;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}
}
