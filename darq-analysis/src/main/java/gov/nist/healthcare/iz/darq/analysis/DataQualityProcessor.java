package gov.nist.healthcare.iz.darq.analysis;

import java.util.ArrayList;
import java.util.List;

import gov.nist.healthcare.iz.darq.analysis.service.DataQualityCheck;
import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;

public class DataQualityProcessor {

	private List<DataQualityCheck> inspectors;

	public DataQualityProcessor(List<DataQualityCheck> inspectors) {
		super();
		this.inspectors = inspectors;
	}
	
	public List<Detection> inspect(AggregatePatientRecord record){
		List<Detection> detections = new ArrayList<>();
		for(DataQualityCheck dqc : inspectors){
			detections.addAll(dqc.inspect(record));
		}
		return detections;
	}
	
	
}
