package gov.nist.healthcare.iz.darq.digest.service;

import gov.nist.healthcare.iz.darq.digest.domain.Fraction;

public interface ProgressReporter {

	void inform(Fraction progress);
	
}
