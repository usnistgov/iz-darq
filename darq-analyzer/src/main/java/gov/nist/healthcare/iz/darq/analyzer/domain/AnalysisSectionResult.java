package gov.nist.healthcare.iz.darq.analyzer.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisPayload.FieldValue;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisPayload.Options;
import gov.nist.healthcare.iz.darq.digest.domain.Field._CG;
import gov.nist.healthcare.iz.darq.digest.domain.Fraction;

public class AnalysisSectionResult {
	
	public static class AnalysisPayloadResult {
		private _CG type;
		private List<FieldValue> filters;
		private List<Set<FieldValue>> groups;
		private Map<String, Fraction> values;
		private boolean distribution;
		private Options display;
		
		public AnalysisPayloadResult() {
			super();
			filters = new ArrayList<>();
			groups = new ArrayList<>();
			values = new HashMap<>();
			display = new Options();
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

		public Map<String, Fraction> getValues() {
			return values;
		}
		public void setValues(Map<String, Fraction> values) {
			this.values = values;
		}

		public Options getDisplay() {
			return display;
		}
		public void setDisplay(Options display) {
			this.display = display;
		}

		public _CG getType() {
			return type;
		}

		public void setType(_CG type) {
			this.type = type;
		}

		public boolean isDistribution() {
			return distribution;
		}

		public void setDistribution(boolean distribution) {
			this.distribution = distribution;
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
