package gov.nist.healthcare.iz.darq.digest.service.impl;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import gov.nist.healthcare.iz.darq.digest.domain.DetectionSum;
import gov.nist.healthcare.iz.darq.digest.domain.PatientPayload;
import gov.nist.healthcare.iz.darq.digest.domain.TablePayload;
import gov.nist.healthcare.iz.darq.digest.domain.VaccinationPayload;
import gov.nist.healthcare.iz.darq.digest.service.AgeGroupService;
import gov.nist.healthcare.iz.darq.digest.service.GroupService;

@Component
public class SimpleGroupService implements GroupService {

	
	public Map<String, Map<String, VaccinationPayload>> makeVxSectionProvider(AgeGroupService ageGroupCalculator, Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, Integer>>>>>> vx, Map<String, Map<String, Map<String, DetectionSum>>> dqa, Map<String, Map<String, Map<String, TablePayload>>> codes){
		Map<String, Map<String, VaccinationPayload>> vxSection = new HashMap<>();
		
		for(String provider : vx.keySet()){
			vxSection.put(provider, makeVxSectionAge(ageGroupCalculator, vx.get(provider), dqa.get(provider), codes.get(provider)));
		}

		return vxSection;
	}
	
	
	public Map<String, VaccinationPayload> makeVxSectionAge(AgeGroupService ageGroupCalculator, Map<String, Map<String, Map<String, Map<String, Map<String, Integer>>>>> vx, Map<String, Map<String, DetectionSum>> dqa, Map<String, Map<String, TablePayload>> codes){
		Map<String, VaccinationPayload> vxSection = new HashMap<>();
		
		for(String grp : ageGroupCalculator.getGroups()){
			if((vx != null && vx.containsKey(grp)) || (dqa != null && dqa.containsKey(grp)) || (codes != null && codes.containsKey(grp))){
				vxSection.put(grp, makeVxPayload(vx != null ? vx.get(grp) : new HashMap<>(), dqa != null ? dqa.get(grp) : new HashMap<>(), codes != null ? codes.get(grp) : new HashMap<>()));
			}
		}
		
		return vxSection;
	}
	
	public VaccinationPayload makeVxPayload(Map<String, Map<String, Map<String, Map<String, Integer>>>> vx, Map<String, DetectionSum> dqa, Map<String, TablePayload> codes){
		return new VaccinationPayload(dqa, codes, vx);
	}
	
	public Map<String, PatientPayload> makePatSectionAge(AgeGroupService ageGroupCalculator, Map<String, Map<String, DetectionSum>> dqa, Map<String, Map<String, TablePayload>> codes){
		Map<String, PatientPayload> patSection = new HashMap<>();
		
		for(String grp : ageGroupCalculator.getGroups()){
			if((dqa != null && dqa.containsKey(grp)) || (codes != null && codes.containsKey(grp))){
				patSection.put(grp, makePatPayload(dqa != null ? dqa.get(grp) :  new HashMap<>(), codes != null ? codes.get(grp) : new HashMap<>()));
			}
		}

		return patSection;
	}
	
	public PatientPayload makePatPayload(Map<String, DetectionSum> dqa, Map<String, TablePayload> codes){
		return new PatientPayload(dqa, codes, 1);
	}

}
