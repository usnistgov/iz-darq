package gov.nist.healthcare.iz.darq.analysis;

import java.util.ArrayList;
import java.util.List;

import gov.nist.healthcare.iz.darq.analysis.service.StatisticCalculator;

public class AnalysisRawResult {
	
	private String id;
	private int totalRecords = 0;
	private List<SubjectAnalysis> fields;
	private List<Detection> detections;
	
	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}
	
	
	public List<SubjectAnalysis> getFields() {
		if(fields == null){
			fields = new ArrayList<>();
		}
		return fields;
	}

	public void setFields(List<SubjectAnalysis> fields) {
		this.fields = fields;
	}

	public void inc(){
		this.totalRecords++;
	}
	
	public void factorIn(AnalysisRawResult x){
		this.totalRecords += x.getTotalRecords();
		StatisticCalculator.merge(this.getFields(), x.getFields());
		this.getDetections().addAll(x.getDetections());
	}
	

	public List<Detection> getDetections() {
		if(this.detections == null){
			this.detections = new ArrayList<>();
		}
		return detections;
	}

	public void setDetections(List<Detection> detections) {
		this.detections = detections;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
	
}
