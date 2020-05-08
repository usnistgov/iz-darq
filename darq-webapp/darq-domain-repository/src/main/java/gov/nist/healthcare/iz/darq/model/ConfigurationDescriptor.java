package gov.nist.healthcare.iz.darq.model;

import java.util.Date;

public class ConfigurationDescriptor {

	private String id;
	private String name;
	private String owner;
	private Date lastUpdated;
	private boolean viewOnly;
	private boolean owned;
	private boolean locked;
	private boolean published;
	
	public ConfigurationDescriptor() {
		super();
	}
	public ConfigurationDescriptor(
			String id,
			String name,
			String owner,
			Date lastUpdated,
			boolean locked,
			boolean published,
			boolean owned,
			boolean viewOnly
	) {
		super();
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.lastUpdated = lastUpdated;
		this.locked = locked;
		this.published = published;
		this.owned = owned;
		this.viewOnly = viewOnly;
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
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
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
	public boolean isPublished() {
		return published;
	}
	public void setPublished(boolean published) {
		this.published = published;
	}
	public boolean isOwned() {
		return owned;
	}
	public void setOwned(boolean owned) {
		this.owned = owned;
	}
}
