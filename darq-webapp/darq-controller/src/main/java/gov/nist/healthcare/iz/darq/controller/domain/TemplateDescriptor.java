package gov.nist.healthcare.iz.darq.controller.domain;

import java.util.List;

import gov.nist.healthcare.iz.darq.model.ConfigurationDescriptor;

public class TemplateDescriptor {

	String id;
	String name;
	String owner;
	List<ConfigurationDescriptor> compatibilities;
	
	public TemplateDescriptor(String id, String name, String owner, List<ConfigurationDescriptor> compatibilities) {
		super();
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.compatibilities = compatibilities;
	}
	
	public TemplateDescriptor() {
		super();
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
	public List<ConfigurationDescriptor> getCompatibilities() {
		return compatibilities;
	}
	public void setCompatibilities(List<ConfigurationDescriptor> compatibilities) {
		this.compatibilities = compatibilities;
	}
	
	
}
