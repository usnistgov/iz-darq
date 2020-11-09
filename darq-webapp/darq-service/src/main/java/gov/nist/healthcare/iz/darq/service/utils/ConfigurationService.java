package gov.nist.healthcare.iz.darq.service.utils;

import java.util.List;

import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.model.DigestConfiguration;

public interface ConfigurationService {
	List<DigestConfiguration> compatibilities(ConfigurationPayload payload, List<DigestConfiguration> configurations);
	boolean compatible(ConfigurationPayload master, ConfigurationPayload slave);
}
