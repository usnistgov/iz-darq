package gov.nist.healthcare.iz.darq.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Range;
import gov.nist.healthcare.iz.darq.model.DigestConfiguration;
import gov.nist.healthcare.iz.darq.service.utils.ConfigurationService;

@Service
public class SimpleConfigurationService implements ConfigurationService {
	
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
		
		return age_groups && slave.getAgeGroups().size() == master.getAgeGroups().size() && detections;
	}


}
