package gov.nist.healthcare.iz.darq.digest.service;

import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Fraction;
import gov.nist.healthcare.iz.darq.parser.type.DqDateFormat;
import java.nio.file.Path;

public interface DigestRunner {
	
	void digest(ConfigurationPayload configuration, String patient, String vaccines, DqDateFormat dateFormat, Path output, Path directory) throws Exception;
	Fraction spy();

}
