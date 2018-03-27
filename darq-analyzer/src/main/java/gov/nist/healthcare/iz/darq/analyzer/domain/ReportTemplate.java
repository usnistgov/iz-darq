package gov.nist.healthcare.iz.darq.analyzer.domain;

import java.util.List;

public class ReportTemplate {
	
	String id;
	String name;
	String description;
	List<ReportSection> sections;

	public List<ReportSection> getSections() {
		return sections;
	}

	public void setSections(List<ReportSection> sections) {
		this.sections = sections;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
