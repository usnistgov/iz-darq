package gov.nist.healthcare.iz.darq.service.utils;

import java.util.List;

import gov.nist.healthcare.iz.darq.configuration.exception.InvalidConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Range;
import gov.nist.healthcare.iz.darq.model.DigestConfiguration;

public interface ConfigurationService {
	List<DigestConfiguration> compatibilities(ConfigurationPayload payload, List<DigestConfiguration> configurations);

	boolean compatible(ConfigurationPayload source, ConfigurationPayload target);

	void validateConfigurationPayload(ConfigurationPayload configurationPayload, boolean validateMismoConfiguration) throws InvalidConfigurationPayload;

	void validateAgeGroups(List<Range> ageGroups, boolean allowEmptyGroupSameMinAndMax) throws InvalidConfigurationPayload;
}
