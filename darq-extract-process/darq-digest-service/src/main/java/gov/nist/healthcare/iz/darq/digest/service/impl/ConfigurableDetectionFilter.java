package gov.nist.healthcare.iz.darq.digest.service.impl;

import gov.nist.healthcare.iz.darq.digest.service.DetectionFilter;

import java.util.HashSet;
import java.util.Set;

public class ConfigurableDetectionFilter implements DetectionFilter {
	
	Set<String> included;
	
	public ConfigurableDetectionFilter(Set<String> detections){
		if(detections == null){
			included = new HashSet<>();
		} else {
			this.included = detections;
		}
	}
	
	@Override
	public boolean in(String code) {
		return included.contains(code);
	}

}
