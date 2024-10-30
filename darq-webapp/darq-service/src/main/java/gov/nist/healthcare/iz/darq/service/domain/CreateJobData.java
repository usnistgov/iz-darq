package gov.nist.healthcare.iz.darq.service.domain;

public class CreateJobData {
	private String name;
	private String facilityId;
	private String ownerId;

	public CreateJobData() {
	}

	public CreateJobData(String name, String ownerId) {
		this.name = name;
		this.ownerId = ownerId;
	}

	public CreateJobData(String name, String facilityId, String ownerId) {
		this.name = name;
		this.facilityId = facilityId;
		this.ownerId = ownerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
}
