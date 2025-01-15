package gov.nist.healthcare.iz.darq.service.impl;

import java.util.ArrayList;
import java.util.List;

import gov.nist.healthcare.iz.darq.configuration.exception.InvalidConfigurationPayload;
import gov.nist.healthcare.iz.darq.configuration.validation.ConfigurationPayloadValidator;
import gov.nist.healthcare.iz.darq.digest.domain.expression.ComplexDetection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Range;
import gov.nist.healthcare.iz.darq.model.DigestConfiguration;
import gov.nist.healthcare.iz.darq.service.utils.ConfigurationService;

@Service
public class SimpleConfigurationService implements ConfigurationService {

	@Autowired
	private ConfigurationPayloadValidator configurationPayloadValidator;
	
	@Override
	public List<DigestConfiguration> compatibilities(ConfigurationPayload payload, List<DigestConfiguration> configurations) {
		List<DigestConfiguration> descriptors = new ArrayList<>();
		if(configurations != null) {
			for(DigestConfiguration c : configurations){
				if(this.compatible(payload, c.getPayload())){
					descriptors.add(c);
				}
			}
		}
		return descriptors;
	}

	@Override
	public boolean compatible(ConfigurationPayload source, ConfigurationPayload target) {
		boolean age_groups = true;
		boolean detections = true;
		boolean complexDetections = true;

		if(source == null) {
			return false;
		}
		//detections
		if(source.getDetections() != null && target.getDetections() != null){
			for(String d : source.getDetections()){
				if(!target.getDetections().contains(d)){
					detections = false;
					break;
				}
			}
		}
		else {
			detections = target.getDetections() == null;
		}
		
		
		//ageGroups
		for(Range r : target.getAgeGroups()){
			if(!source.getAgeGroups().contains(r)){
				age_groups = false;
				break;
			}
		}

		// Complex Detections
		if(source.getComplexDetections() != null && target.getComplexDetections() != null){
			for(ComplexDetection d : source.getComplexDetections()){
				if(!target.getComplexDetections().contains(d)){
					complexDetections = false;
					break;
				}
			}
		}
		
		return age_groups && target.getAgeGroups().size() == source.getAgeGroups().size() && detections && complexDetections;
	}

	@Override
	public void validateConfigurationPayload(ConfigurationPayload configurationPayload, boolean validateMismoConfiguration) throws InvalidConfigurationPayload {
		this.configurationPayloadValidator.validateConfigurationPayload(configurationPayload, validateMismoConfiguration);
	}

	@Override
	public void validateAgeGroups(List<Range> ageGroups, boolean allowEmptyGroupSameMinAndMax) throws InvalidConfigurationPayload {
		List<String> errors = this.configurationPayloadValidator.validateAgeGroups(ageGroups, allowEmptyGroupSameMinAndMax);
		if(errors.size() > 0) {
			throw new InvalidConfigurationPayload(errors);
		}
	}
}
