package gov.nist.healthcare.iz.darq.digest.service.impl;

import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.service.AgeGroupService;
import gov.nist.healthcare.iz.darq.digest.service.ConfigurationProvider;
import gov.nist.healthcare.iz.darq.digest.service.DetectionFilter;

public class SimpleConfigurationProvider implements ConfigurationProvider {

	AgeGroupService ageGroup;
	DetectionFilter filter;
	
	public SimpleConfigurationProvider(ConfigurationPayload payload) {
		super();
		this.ageGroup = new AgeGroupCalculator(payload.getAgeGroups());
		this.filter = new ConfigurableDetectionFilter(payload.getDetections());
	}

	@Override
	public AgeGroupService ageGroupService() {
		return this.ageGroup;
	}

	@Override
	public DetectionFilter detectionFilter() {
		return this.filter;
	}
	
	

}
