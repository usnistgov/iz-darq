package gov.nist.healthcare.iz.darq.analyzer.domain;

import java.util.ArrayList;
import java.util.List;

public class AnalysisReport {
	
	private String name;
	private String description;
	private List<AnalysisSectionResult> sections;
	
	
	public AnalysisReport() {
		super();
		this.sections = new ArrayList<>();
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
	public List<AnalysisSectionResult> getSections() {
		return sections;
	}
	public void setSections(List<AnalysisSectionResult> sections) {
		this.sections = sections;
	}

}
