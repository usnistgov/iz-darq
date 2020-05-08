package gov.nist.healthcare.iz.darq.service.utils;

import java.util.List;

import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.model.ConfigurationDescriptor;
import gov.nist.healthcare.iz.darq.model.DigestConfiguration;

public interface ConfigurationService {

	List<ConfigurationDescriptor> compatibilities(ConfigurationPayload payload, List<DigestConfiguration> configurations, String user);
	boolean compatible(ConfigurationPayload master, ConfigurationPayload slave);
	ConfigurationDescriptor extract(DigestConfiguration config, String user);
	boolean isViewOnlyForUser(DigestConfiguration config, String user);
}
