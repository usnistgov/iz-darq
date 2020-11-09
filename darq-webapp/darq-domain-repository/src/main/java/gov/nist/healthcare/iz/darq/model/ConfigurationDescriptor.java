package gov.nist.healthcare.iz.darq.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nist.healthcare.domain.trait.Owned;
import gov.nist.healthcare.domain.trait.Publishable;

import java.util.Date;

public class ConfigurationDescriptor implements Owned, Publishable {

	private String id;
	private String name;
	@Deprecated
	private String owner;
	private String ownerDisplayName;
	private String ownerId;
	private Date lastUpdated;
	private boolean locked;
	private boolean published;
	
	public ConfigurationDescriptor() {
		super();
	}
	public ConfigurationDescriptor(
			String id,
			String name,
			String owner,
			String ownerId,
			String ownerDisplayName,
			Date lastUpdated,
			boolean locked,
			boolean published
	) {
		super();
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.ownerId = ownerId;
		this.ownerDisplayName = ownerDisplayName;
		this.lastUpdated = lastUpdated;
		this.locked = locked;
		this.published = published;
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
	@Deprecated
	@JsonIgnore
	public String getOwner() {
		return owner;
	}
	@Deprecated
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	public boolean isPublished() {
		return published;
	}
	public void setPublished(boolean published) {
		this.published = published;
	}

	public String getOwnerDisplayName() {
		return ownerDisplayName;
	}

	public void setOwnerDisplayName(String ownerDisplayName) {
		this.ownerDisplayName = ownerDisplayName;
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
	@JsonProperty("public")
	public boolean isPublic() {
		return this.isPublished();
	}
}
