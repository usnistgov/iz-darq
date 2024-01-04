package gov.nist.healthcare.iz.darq.patient.matching.model.mismo;

import gov.nist.healthcare.iz.darq.patient.matching.model.MatchResult;

public class MismoMatchResult extends MatchResult {

  private String signature;

  public MismoMatchResult(boolean match, String signature) {
	super(match);
	this.signature = signature;
  }

  public String getSignature() {
	return signature;
  }

  public void setSignature(String signature) {
	this.signature = signature;
  }

}
