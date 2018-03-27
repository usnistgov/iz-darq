package gov.nist.healthcare.iz.darq.service.utils;

import java.util.List;

import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.model.ConfigurationDescriptor;
import gov.nist.healthcare.iz.darq.model.DigestConfiguration;

public interface ConfigurationService {

	public List<ConfigurationDescriptor> compatibilities(ConfigurationPayload payload, List<DigestConfiguration> configurations);
	public boolean compatible(ConfigurationPayload master, ConfigurationPayload slave);
	public ConfigurationDescriptor extract(DigestConfiguration config);
}
