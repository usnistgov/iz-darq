package gov.nist.healthcare.iz.darq.digest.service;

import gov.nist.healthcare.iz.darq.digest.domain.ADChunk;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Fraction;

public interface DigestRunner {
	
	ADChunk digest(ConfigurationPayload configuration, String patient, String vaccines) throws Exception;
	Fraction spy();

}
