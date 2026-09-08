package gov.nist.healthcare.iz.darq.digest.service.impl;

import gov.nist.healthcare.iz.darq.digest.service.VaxGroupMapper;

import java.util.Map;

public class SimpleVaxGroupMapper implements VaxGroupMapper {

	private Map<String, String> codes;

	public SimpleVaxGroupMapper(Map<String, String> codes) {
		super();
		this.codes = codes;
	}

	@Override
	public String translate(String code) {
		if(codes != null && codes.containsKey(code)){
			return this.codes.get(code);
		}
		return code;
	}
	
	
}
