package gov.nist.healthcare.iz.darq.digest.domain;

import java.util.Date;

public class ADFMetaData {
	String id;
	String name;
	String path;
	String owner;
	Date analysedOn;
	Date uploadedOn;
	String size;
	String keyHash;
	ConfigurationPayload configuration;
	Summary summary;
	
	
	public ADFMetaData(String name, String path, String owner, Date analysedOn, Date uploadedOn,
			ConfigurationPayload configuration, String keyHash, Summary summary, String size) {
		super();
		this.name = name;
		this.path = path;
		this.keyHash = keyHash;
		this.owner = owner;
		this.analysedOn = analysedOn;
		this.uploadedOn = uploadedOn;
		this.configuration = configuration;
		this.summary = summary;
		this.size = size;
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
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
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
}
