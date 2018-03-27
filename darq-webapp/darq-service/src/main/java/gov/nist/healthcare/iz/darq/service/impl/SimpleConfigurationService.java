package gov.nist.healthcare.iz.darq.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Range;
import gov.nist.healthcare.iz.darq.model.ConfigurationDescriptor;
import gov.nist.healthcare.iz.darq.model.DigestConfiguration;
import gov.nist.healthcare.iz.darq.service.utils.ConfigurationService;

@Service
public class SimpleConfigurationService implements ConfigurationService {
	
	@Override
	public List<ConfigurationDescriptor> compatibilities(ConfigurationPayload payload, List<DigestConfiguration> configurations) {
		List<ConfigurationDescriptor> descriptors = new ArrayList<>();
		for(DigestConfiguration c : configurations){
			if(this.compatible(payload, c.getPayload())){
				descriptors.add(this.extract(c));
			}
		}
		return descriptors;
	}

	@Override
	public boolean compatible(ConfigurationPayload master, ConfigurationPayload slave) {
		boolean age_groups = true;
		boolean detections = true;
		
		//detections
		if(master.getDetections() != null && slave.getDetections() != null){
			for(String d : slave.getDetections()){
				if(!master.getDetections().contains(d)){
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
		
		return age_groups && detections;
	}
	
	@Override
	public ConfigurationDescriptor extract(DigestConfiguration config){
		return new ConfigurationDescriptor(config.getId(), config.getName(), config.getOwner(), config.getLastUpdated());
	}

}
