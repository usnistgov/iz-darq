package gov.nist.healthcare.iz.darq.model;

import java.util.Date;

import org.springframework.data.annotation.Transient;

import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;

public class DigestConfiguration {

	private String id;
	private String name;
	private String owner;
	private Date lastUpdated;
	private ConfigurationPayload payload;
	private String description;
	private boolean published;
	private boolean locked;
	@Transient
	private boolean viewOnly;
	
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
	public ConfigurationPayload getPayload() {
		return payload;
	}
	public void setPayload(ConfigurationPayload payload) {
		this.payload = payload;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public boolean isViewOnly() {
		return viewOnly;
	}
	public void setViewOnly(boolean viewOnly) {
		this.viewOnly = viewOnly;
	}
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
}
