package gov.nist.healthcare.iz.darq.service.impl;

import java.util.ArrayList;
import java.util.List;

import gov.nist.healthcare.iz.darq.configuration.exception.InvalidConfigurationPayload;
import gov.nist.healthcare.iz.darq.configuration.validation.ConfigurationPayloadValidator;
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
	public boolean compatible(ConfigurationPayload master, ConfigurationPayload slave) {
		boolean age_groups = true;
		boolean detections = true;
		if(master == null) {
			return false;
		}
		//detections
		if(master.getDetections() != null && slave.getDetections() != null){
			for(String d : master.getDetections()){
				if(!slave.getDetections().contains(d)){
					detections = false;
					break;
				}
			}
		}
		else {
			detections = slave.getDetections() == null;
		}
		
		
		//ageGroups
		for(Range r : slave.getAgeGroups()){
			if(!master.getAgeGroups().contains(r)){
				age_groups = false;
				break;
			}
		}
		
		return age_groups && slave.getAgeGroups().size() == master.getAgeGroups().size() && detections;
	}

	@Override
	public void validateConfigurationPayload(ConfigurationPayload configurationPayload) throws InvalidConfigurationPayload {
		this.configurationPayloadValidator.validateConfigurationPayload(configurationPayload);
	}

	@Override
	public void validateAgeGroups(List<Range> ageGroups, boolean allowEmptyGroupSameMinAndMax) throws InvalidConfigurationPayload {
		List<String> errors = this.configurationPayloadValidator.validateAgeGroups(ageGroups, allowEmptyGroupSameMinAndMax);
		if(errors.size() > 0) {
			throw new InvalidConfigurationPayload(errors);
		}
	}
}
