package gov.nist.healthcare.iz.darq.patient.matching.model;

import java.util.HashMap;
import java.util.Map;

public class RecordMatchProcessResult {
	private boolean duplicate;
	private Map<String, String> duplicatesWithSignature;

	public RecordMatchProcessResult() {
		duplicate = false;
		duplicatesWithSignature = new HashMap<>();
	}

	public boolean isDuplicate() {
		return duplicate;
	}

	public void setDuplicate(boolean duplicate) {
		this.duplicate = duplicate;
	}

	public Map<String, String> getDuplicatesWithSignature() {
		return duplicatesWithSignature;
	}

	public void setDuplicatesWithSignature(Map<String, String> duplicatesWithSignature) {
		this.duplicatesWithSignature = duplicatesWithSignature;
	}
}
