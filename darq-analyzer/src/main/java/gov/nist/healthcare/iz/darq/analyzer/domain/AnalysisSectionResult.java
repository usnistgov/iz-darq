package gov.nist.healthcare.iz.darq.analyzer.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisPayload.FieldValue;

public class AnalysisSectionResult {
	
	public static class AnalysisPayloadResult {
		private List<FieldValue> filters;
		private List<Set<FieldValue>> groups;
		private Map<String, Double> values;
		
		
		public AnalysisPayloadResult() {
			super();
			filters = new ArrayList<>();
			groups = new ArrayList<>();
			values = new HashMap<>();
		}
		
		public List<FieldValue> getFilters() {
			return filters;
		}
		public void setFilters(List<FieldValue> filters) {
			this.filters = filters;
		}
		public List<Set<FieldValue>> getGroups() {
			return groups;
		}
		public void setGroups(List<Set<FieldValue>> groups) {
			this.groups = groups;
		}
		public Map<String, Double> getValues() {
			return values;
		}
		public void setValues(Map<String, Double> values) {
			this.values = values;
		}
	}
	
	private String title;
	private String description;
	private List<AnalysisPayloadResult> results;
	
	public AnalysisSectionResult(){
		results = new ArrayList<>();
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<AnalysisPayloadResult> getResults() {
		return results;
	}
	public void setResults(List<AnalysisPayloadResult> results) {
		this.results = results;
	}
	
}
