package gov.nist.healthcare.iz.darq.patient.matching.model;

import java.util.HashMap;
import java.util.Map;

public class RecordMatchProcessResult {
	private boolean duplicate;
	private final Map<String, Integer> signatures;

	public RecordMatchProcessResult() {
		duplicate = false;
		signatures = new HashMap<>();
	}

	public boolean isDuplicate() {
		return duplicate;
	}

	public void setDuplicate(boolean duplicate) {
		this.duplicate = duplicate;
	}

	public Map<String, Integer> getSignatures() {
		return signatures;
	}
}
