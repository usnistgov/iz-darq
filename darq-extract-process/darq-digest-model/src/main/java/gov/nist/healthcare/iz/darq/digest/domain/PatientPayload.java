package gov.nist.healthcare.iz.darq.digest.domain;

import java.util.Map;

public class PatientPayload {
	
	private int count;
	private Map<String, DetectionSum> detection;
	private Map<String, TablePayload> codeTable;
	
	public PatientPayload(Map<String, DetectionSum> detection, Map<String, TablePayload> codeTable, int i) {
		super();
		this.detection = detection;
		this.codeTable = codeTable;
		this.count = i;
	}
		
	public PatientPayload() {
		super();
	}

	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public Map<String, DetectionSum> getDetection() {
		return detection;
	}
	public void setDetection(Map<String, DetectionSum> detection) {
		this.detection = detection;
	}
	public Map<String, TablePayload> getCodeTable() {
		return codeTable;
	}
	public void setCodeTable(Map<String, TablePayload> codeTable) {
		this.codeTable = codeTable;
	}

	@Override
	public String toString() {
		return "PatientPayload [count=" + count + ", detection=" + detection + ", codeTable=" + codeTable + "]";
	}
	
	
	
}
