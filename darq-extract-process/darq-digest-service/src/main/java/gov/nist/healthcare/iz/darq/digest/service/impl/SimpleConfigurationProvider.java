package gov.nist.healthcare.iz.darq.digest.service.impl;

import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.service.AgeGroupService;
import gov.nist.healthcare.iz.darq.digest.service.ConfigurationProvider;
import gov.nist.healthcare.iz.darq.digest.service.DetectionFilter;
import gov.nist.healthcare.iz.darq.digest.service.VaxGroupMapper;

public class SimpleConfigurationProvider implements ConfigurationProvider {

	AgeGroupService ageGroup;
	DetectionFilter filter;
	VaxGroupMapper vaxGroupMapperObj;
	
	public SimpleConfigurationProvider(ConfigurationPayload payload) {
		super();
		this.ageGroup = new AgeGroupCalculator(payload.getAgeGroups());
		this.filter = new ConfigurableDetectionFilter(payload.getDetections());
		this.vaxGroupMapperObj = new SimpleVaxGroupMapper(payload.getVaxCodeAbstraction());
	}

	@Override
	public AgeGroupService ageGroupService() {
		return this.ageGroup;
	}

	@Override
	public DetectionFilter detectionFilter() {
		return this.filter;
	}

	@Override
	public VaxGroupMapper vaxGroupMapper() {
		return this.vaxGroupMapperObj;
	}
	
	

}
