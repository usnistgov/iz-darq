package gov.nist.healthcare.iz.darq.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nist.healthcare.domain.trait.AssignableToFacility;
import gov.nist.healthcare.domain.trait.Owned;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class ADFMergeJobDescriptor implements Owned, AssignableToFacility {
	@Id
	private String id;
	private String name;
	private List<String> tags;
	private Date submitTime;
	private Date startTime;
	private Date endTime;
	private JobStatus status;
	@Deprecated
	private String owner;
	private String ownerId;
	private String ownerDisplayName;
	private String failure;
	private List<ADFDescriptor> adfList;
	private String facilityId;
	private String mergedAdfId;

	public ADFMergeJobDescriptor(
			String id,
			String name,
			List<String> tags,
			Date submitTime,
			Date startTime,
			Date endTime,
			JobStatus status,
			String owner,
			String ownerId,
			String ownerDisplayName,
			String failure,
			List<ADFDescriptor> adfList,
			String facilityId,
			String mergedAdfId
	) {
		this.id = id;
		this.name = name;
		this.tags = tags;
		this.submitTime = submitTime;
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status;
		this.owner = owner;
		this.ownerId = ownerId;
		this.ownerDisplayName = ownerDisplayName;
		this.failure = failure;
		this.adfList = adfList;
		this.facilityId = facilityId;
		this.mergedAdfId = mergedAdfId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public JobStatus getStatus() {
		return status;
	}

	public void setStatus(JobStatus status) {
		this.status = status;
	}

	public String getFailure() {
		return failure;
	}

	public void setFailure(String failure) {
		this.failure = failure;
	}

	public List<ADFDescriptor> getAdfList() {
		return adfList;
	}

	public void setAdfList(List<ADFDescriptor> adfList) {
		this.adfList = adfList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getEndTime() {
		return endTime;
	}

	public Date getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getOwnerDisplayName() {
		return ownerDisplayName;
	}

	public void setOwnerDisplayName(String ownerDisplayName) {
		this.ownerDisplayName = ownerDisplayName;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	@Override
	@JsonProperty("owner")
	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
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

	public String getMergedAdfId() {
		return mergedAdfId;
	}

	public void setMergedAdfId(String mergedAdfId) {
		this.mergedAdfId = mergedAdfId;
	}
}
