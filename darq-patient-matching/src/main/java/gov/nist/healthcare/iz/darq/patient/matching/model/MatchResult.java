package gov.nist.healthcare.iz.darq.patient.matching.model;

public class MatchResult {
  private boolean match;

  public MatchResult(boolean match) {
	this.match = match;
  }

  public boolean isMatch() {
	return match;
  }

  public void setMatch(boolean match) {
	this.match = match;
  }
}
