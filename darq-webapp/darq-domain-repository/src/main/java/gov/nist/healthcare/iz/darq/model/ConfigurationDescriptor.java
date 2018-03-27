package gov.nist.healthcare.iz.darq.model;

import java.util.Date;

public class ConfigurationDescriptor {

	private String id;
	private String name;
	private String owner;
	private Date lastUpdated;
	
	
	
	public ConfigurationDescriptor() {
		super();
	}
	public ConfigurationDescriptor(String id, String name, String owner, Date lastUpdated) {
		super();
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.lastUpdated = lastUpdated;
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

}
