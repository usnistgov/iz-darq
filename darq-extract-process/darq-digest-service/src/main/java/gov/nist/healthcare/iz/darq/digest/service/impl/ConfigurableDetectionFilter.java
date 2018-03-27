package gov.nist.healthcare.iz.darq.digest.service.impl;

import java.util.List;

import gov.nist.healthcare.iz.darq.digest.service.DetectionFilter;

public class ConfigurableDetectionFilter implements DetectionFilter {
	
	List<String> considered;
	
	public ConfigurableDetectionFilter(List<String> x){
		this.considered = x;
	}
	
	@Override
	public boolean in(String code) {
		return considered.contains(code);
	}

}
