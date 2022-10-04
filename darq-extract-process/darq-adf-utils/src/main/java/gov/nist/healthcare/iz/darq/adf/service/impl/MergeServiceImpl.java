package gov.nist.healthcare.iz.darq.adf.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.nist.healthcare.iz.darq.digest.domain.*;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.iz.darq.adf.service.MergeService;

@Service
public class MergeServiceImpl implements MergeService {

	@Override
	public synchronized ADChunk mergeChunk(ADChunk a, ADChunk b){
		a.setReportingGroupPayload(mergeADPayloadProvider(a.getReportingGroupPayload(), b.getReportingGroupPayload()));
		a.setGeneralPatientPayload(mergePatientAgeGroup(a.getGeneralPatientPayload(), b.getGeneralPatientPayload()));
		a.setExtraction(mergeExtract(a.getExtraction(), b.getExtraction()));
		a.setProviders(mergeProvider(a.getProviders(), b.getProviders()));
		a.setUnreadVaccinations(a.getUnreadVaccinations() + b.getUnreadVaccinations());
		a.setUnreadPatients(a.getUnreadPatients() + b.getUnreadPatients());
		a.setNbPatients(a.getNbPatients() + b.getNbPatients());
		a.setNbVaccinations(a.getNbVaccinations() + b.getNbVaccinations());
		a.setMaxVaccination(b.getNbVaccinations());
		a.setMinVaccination(b.getNbVaccinations());
		a.setCodes(mergeCodeValues(a.getCodes(), b.getCodes()));
		a.setValues(mergeFieldValues(a.getValues(), b.getValues()));
		a.setAdministered(a.getAdministered() + b.getAdministered());
		a.setHistorical(a.getHistorical() + b.getHistorical());
		return a;
	}
	
	
	public Map<String, String> mergeProvider(Map<String, String> a, Map<String, String> b){
		Map<String, String> x =  a != null ? new HashMap<>(a) : new HashMap<>();
		if(b != null){
			b.forEach((k, v) -> x.merge(k, v, this::mergeProvider));
		}
		return x;
	}
	
	public Map<String, Set<String>> mergeCodeValues(Map<String, Set<String>> a, Map<String, Set<String>> b){
		Map<String, Set<String>> x =   a != null ? new HashMap<>(a) : new HashMap<>();
		if(b != null){
			b.forEach((k, v) -> x.merge(k, v, this::mergeSet));
		}
		return x;
	}
	
	public Map<Field, Set<String>> mergeFieldValues(Map<Field, Set<String>> a, Map<Field, Set<String>> b){
		Map<Field, Set<String>> x =   a != null ? new HashMap<>(a) : new HashMap<>();
		if(b != null){
			b.forEach((k, v) -> x.merge(k, v, this::mergeSet));
		}
		return x;
	}
	
	public Set<String> mergeSet(Set<String> a, Set<String> b){
		 Set<String> x = a != null ? new HashSet<>(a) : new HashSet<>();
		 if(b != null){
			 x.addAll(b);
		 }
		 return x;
	}
	
	public Map<String, ExtractFraction> mergeExtract(Map<String, ExtractFraction> a, Map<String, ExtractFraction> b){
		Map<String, ExtractFraction> x =  a != null ? new HashMap<>(a) : new HashMap<>();
		if(b != null){
			b.forEach((k, v) -> x.merge(k, v, ExtractFraction::merge));
		}
		return x;
	}
	
	public String mergeProvider(String a, String b){
		if(!a.equals(b))
			return "ERROR";
		else
			return a;
	}

	@Override
	public Map<String, Map<String, ADPayload>> mergeADPayloadProvider(Map<String, Map<String, ADPayload>> a, Map<String, Map<String, ADPayload>> b){
		Map<String, Map<String, ADPayload>> x =  a != null ? new HashMap<>(a) : new HashMap<>();
		if(b != null){
			b.forEach((k, v) -> x.merge(k, v, this::mergeADPayloadAgeGroup));
		}
		return x;
	}

	@Override
	public Map<String, ADPayload> mergeADPayloadAgeGroup(Map<String, ADPayload> a, Map<String, ADPayload> b){
		Map<String, ADPayload> x = a != null ? new HashMap<>(a) : new HashMap<>();
		if(b != null){
			b.forEach((k, v) -> x.merge(k, v, this::mergeADPayload));
		}
		return x;
	}

	@Override
	public ADPayload mergeADPayload(ADPayload a, ADPayload b){
		return new ADPayload(
				this.mergePatient(a.getPatientPayload(), b.getPatientPayload()),
				this.mergeVxPayload(a.getVaccinationPayload(), b.getVaccinationPayload())
		);
	}
	
	@Override
	public VaccinationPayload mergeVxPayload(VaccinationPayload a, VaccinationPayload b){
		VaccinationPayload vxP = new VaccinationPayload();
		vxP.setTotal(a.getTotal() + b.getTotal());
		vxP.setDetection(mergeDetections(a.getDetection(), b.getDetection()));
		vxP.setCodeTable(mergeCodeTable(a.getCodeTable(), b.getCodeTable()));
		vxP.setVaccinations(mergeVxYear(a.getVaccinations(), b.getVaccinations()));
		return vxP;
	}
	
	@Override
	public Map<String, DetectionSum> mergeDetections(Map<String, DetectionSum> a, Map<String, DetectionSum> b){
		Map<String, DetectionSum> x =  a != null ? new HashMap<>(a) : new HashMap<>();
		if(b != null){
			b.forEach((k, v) -> x.merge(k, v, DetectionSum::merge));
		}
		return x;
	}
	
	@Override
	public Map<String, TablePayload> mergeCodeTable(Map<String, TablePayload> a, Map<String, TablePayload> b){
		Map<String, TablePayload> x =  a != null ? new HashMap<>(a) : new HashMap<>();
		if(b != null){
			b.forEach((k, v) -> x.merge(k, v, this::mergeCode));
		}
		return x;
	}
	
	
	
	@Override
	public TablePayload mergeCode(TablePayload a, TablePayload b){
		TablePayload t =  new TablePayload();
		t.setTotal(a.getTotal() + b.getTotal());
		Map<String, Integer> codes = new HashMap<>(a.getCodes());
		b.getCodes().forEach((k, v) -> codes.merge(k, v, Integer::sum));
		t.setCodes(codes);
		return t;
	}
	
	@Override
	public Map<String, Map<String, Map<String, TablePayload>>> mergeVxYear(Map<String, Map<String, Map<String, TablePayload>>> a, Map<String, Map<String, Map<String, TablePayload>>> b){
		Map<String, Map<String, Map<String, TablePayload>>> x =  a != null ? new HashMap<>(a) : new HashMap<>();
		if(b != null){
			b.forEach((k, v) -> x.merge(k, v, this::mergeVxGender));
		}
		return x;
	}
	
	@Override
	public Map<String, Map<String, TablePayload>> mergeVxGender(Map<String, Map<String, TablePayload>> a, Map<String, Map<String, TablePayload>> b){
		Map<String, Map<String, TablePayload>> x =  a != null ? new HashMap<>(a) : new HashMap<>();
		if(b != null){
			b.forEach((k, v) -> x.merge(k, v, this::mergeCodeTable));
		}
		return x;
	}

	
	@Override
	public Integer merge(Integer a, Integer b){
		return a + b;
	}
	
	@Override
	public Map<String, PatientPayload> mergePatientAgeGroup(Map<String, PatientPayload> a, Map<String, PatientPayload> b){
		Map<String, PatientPayload> x =  a != null ? new HashMap<>(a) : new HashMap<>();
		if(b != null){
			b.forEach((k, v) -> x.merge(k, v, this::mergePatient));
		}
		return x;
	}
	
	@Override
	public PatientPayload mergePatient(PatientPayload a, PatientPayload b){
		PatientPayload pt = new PatientPayload();
		pt.setCount(a.getCount() + b.getCount());
		pt.setDetection(mergeDetections(a.getDetection(), b.getDetection()));
		pt.setCodeTable(mergeCodeTable(a.getCodeTable(), b.getCodeTable()));
		return pt;
	}


}
