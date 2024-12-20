package gov.nist.healthcare.iz.darq.patient.matching.model;

public class MatchResult {
	private boolean match;
	protected String signature;

	public MatchResult(boolean match) {
		this.match = match;
	}

	public boolean isMatch() {
		return match;
	}

	public void setMatch(boolean match) {
		this.match = match;
	}

	public String getSignature() {
		return signature;
	}
}
