package gov.nist.healthcare.iz.darq.controller.domain;

import java.util.Date;
import java.util.List;

import gov.nist.healthcare.iz.darq.digest.domain.ADFMetaData;
import gov.nist.healthcare.iz.darq.model.ConfigurationDescriptor;

public class ADFDescriptor {

	String id;
	String name;
	String owner;
	Date analysedOn;
	Date uploadedOn;
	String size;
	String keyHash;
	List<ConfigurationDescriptor> compatibilities;
	
	
	public ADFDescriptor() {
		super();
	}
	
	public ADFDescriptor(ADFMetaData md, List<ConfigurationDescriptor> compatibilities) {
		super();
		this.id = md.getId();
		this.name = md.getName();
		this.owner = md.getOwner();
		this.analysedOn = md.getAnalysedOn();
		this.uploadedOn = md.getUploadedOn();
		this.keyHash = md.getKeyHash();
		this.size = md.getSize();
		this.compatibilities = compatibilities;
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
}
