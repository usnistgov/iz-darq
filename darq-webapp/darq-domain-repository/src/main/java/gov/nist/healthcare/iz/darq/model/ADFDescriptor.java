package gov.nist.healthcare.iz.darq.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ADFDescriptor {

	String id;
	String name;
	@Deprecated
	String owner;
	String ownerDisplayName;
	String ownerId;
	Date analysedOn;
	Date uploadedOn;
	String size;
	String keyHash;
	List<ConfigurationDescriptor> compatibilities;
	String facilityId;
	
	public ADFDescriptor() {
		super();
	}

	public ADFDescriptor(String id, String name, String owner, String ownerDisplayName, String ownerId, Date analysedOn, Date uploadedOn, String size, List<ConfigurationDescriptor> compatibilities, String facilityId) {
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.ownerDisplayName = ownerDisplayName;
		this.ownerId = ownerId;
		this.analysedOn = analysedOn;
		this.uploadedOn = uploadedOn;
		this.size = size;
		this.compatibilities = compatibilities;
		this.facilityId = facilityId;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
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

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public List<ConfigurationDescriptor> getCompatibilities() {
		return compatibilities;
	}

	public void setCompatibilities(List<ConfigurationDescriptor> compatibilities) {
		this.compatibilities = compatibilities;
	}

	public String getOwnerDisplayName() {
		return ownerDisplayName;
	}

	public void setOwnerDisplayName(String ownerDisplayName) {
		this.ownerDisplayName = ownerDisplayName;
	}

	@JsonProperty("owner")
	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
}
