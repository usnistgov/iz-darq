package gov.nist.healthcare.iz.darq.digest.service;

import gov.nist.healthcare.iz.darq.digest.domain.ADChunk;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Fraction;

import java.util.Optional;

public interface DigestRunner {
	
	ADChunk digest(ConfigurationPayload configuration, String patient, String vaccines, Optional<String> directory) throws Exception;
	Fraction spy();

}
