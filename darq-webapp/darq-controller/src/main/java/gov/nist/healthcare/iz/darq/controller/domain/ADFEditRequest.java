package gov.nist.healthcare.iz.darq.controller.domain;

import java.util.List;

public class ADFEditRequest {
	String name;
	List<String> tags;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}
}
