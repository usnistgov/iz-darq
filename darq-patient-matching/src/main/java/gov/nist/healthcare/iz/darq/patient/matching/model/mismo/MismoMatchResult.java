package gov.nist.healthcare.iz.darq.patient.matching.model.mismo;

import gov.nist.healthcare.iz.darq.patient.matching.model.MatchResult;

public class MismoMatchResult extends MatchResult {

  public MismoMatchResult(boolean match, String signature) {
	super(match);
	this.signature = signature;
  }

}
