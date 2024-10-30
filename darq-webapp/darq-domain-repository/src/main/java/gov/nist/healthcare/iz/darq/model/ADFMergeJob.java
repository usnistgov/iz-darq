package gov.nist.healthcare.iz.darq.model;

import java.util.List;
import java.util.Set;

public class ADFMergeJob extends Job {
	private List<String> tags;
	private Set<String> ids;
	private String mergedAdfId;

	public ADFMergeJob() {
	}

	public ADFMergeJob(String name, List<String> tags, Set<String> ids, String owner, String ownerId, String facilityId) {
		this.setName(name);
		this.tags = tags;
		this.ids = ids;
		this.setStatus(JobStatus.QUEUED);
		this.setFacilityId(facilityId);
		this.setOwner(owner);
		this.setOwnerId(ownerId);
	}


	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public Set<String> getIds() {
		return ids;
	}

	public void setIds(Set<String> ids) {
		this.ids = ids;
	}

	public String getMergedAdfId() {
		return mergedAdfId;
	}

	public void setMergedAdfId(String mergedAdfId) {
		this.mergedAdfId = mergedAdfId;
	}
}
