package gov.nist.healthcare.iz.darq.controller.domain;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nist.healthcare.domain.trait.Owned;
import gov.nist.healthcare.domain.trait.Publishable;
import gov.nist.healthcare.iz.darq.model.ConfigurationDescriptor;

public class TemplateDescriptor implements Owned, Publishable {

	String id;
	String name;
	@Deprecated
	String owner;
	String ownerId;
	protected String ownerDisplayName;
	List<ConfigurationDescriptor> compatibilities;
	private boolean published;
	
	public TemplateDescriptor(
			String id,
			String name,
			String owner,
			String ownerId,
			String ownerDisplayName,
			List<ConfigurationDescriptor> compatibilities,
			boolean published) {
		super();
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.ownerDisplayName = ownerDisplayName;
		this.ownerId = ownerId;
		this.published = published;
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
	@Deprecated
	@JsonIgnore
	public String getOwner() {
		return owner;
	}
	@Deprecated
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public List<ConfigurationDescriptor> getCompatibilities() {
		return compatibilities;
	}
	public void setCompatibilities(List<ConfigurationDescriptor> compatibilities) {
		this.compatibilities = compatibilities;
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
		return this.published;
	}
}
