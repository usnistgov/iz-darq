package gov.nist.healthcare.iz.darq.controller.domain;

import java.util.Date;
import java.util.List;

import gov.nist.healthcare.iz.darq.model.ConfigurationDescriptor;

public class TemplateDescriptor {

	String id;
	String name;
	String owner;
	List<ConfigurationDescriptor> compatibilities;
	boolean viewOnly;
	private boolean owned;
	private boolean published;
	
	public TemplateDescriptor(
			String id,
			String name,
			String owner,
			List<ConfigurationDescriptor> compatibilities,
			boolean owned,
			boolean published,
			boolean viewOnly) {
		super();
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.owned = owned;
		this.published = published;
		this.compatibilities = compatibilities;
		this.viewOnly = viewOnly;
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

	public boolean isOwned() {
		return owned;
	}

	public void setOwned(boolean owned) {
		this.owned = owned;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public boolean isViewOnly() {
		return viewOnly;
	}
	public void setViewOnly(boolean viewOnly) {
		this.viewOnly = viewOnly;
	}
}
