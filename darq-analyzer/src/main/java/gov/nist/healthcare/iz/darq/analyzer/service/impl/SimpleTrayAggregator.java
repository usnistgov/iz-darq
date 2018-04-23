package gov.nist.healthcare.iz.darq.analyzer.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisPayload;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisPayload.FieldValue;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisSectionResult.AnalysisPayloadResult;
import gov.nist.healthcare.iz.darq.analyzer.domain.Tray;
import gov.nist.healthcare.iz.darq.analyzer.service.TrayAggregator;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.domain.Field._CG;
import gov.nist.healthcare.iz.darq.digest.domain.Fraction;


@Service
public class SimpleTrayAggregator implements TrayAggregator {

	@Override
	public AnalysisPayloadResult aggregate(List<Tray> trays, AnalysisPayload payload) {
		AnalysisPayloadResult pr = new AnalysisPayloadResult();
		int weigth = trays.stream().map(x -> {
			return x.getWeigth();
		})
		.reduce(0, (x,y) -> {
			return x + y;
		});
		
		pr.setFilters(payload.getFilters());
		pr.setDisplay(payload.getOptions());
		pr.setType(payload.getType());
		
		for(Tray t : trays){
			
			if(payload.getGroupBy() == null || payload.getGroupBy().size() == 0){
				if(pr.getValues().containsKey("indicator")){
					Fraction v = pr.getValues().get("indicator");
					v.setCount(v.getCount() + t.getCount());
					pr.getValues().put("indicator", v);
				}
				else {
					Fraction v = new Fraction(t.getCount(), getTotal(payload.getType(), t, weigth));
					pr.getValues().put("indicator", v);
				}
			}
			else {
				Set<FieldValue> groupBy = new HashSet<>();
				for(Field group : payload.getGroupBy()){
					String value = t.get(group);
					groupBy.add(new FieldValue(group, value));
				}
				
				if(pr.getGroups().contains(groupBy)){
					int i = pr.getGroups().indexOf(groupBy);
					Fraction v = pr.getValues().get(i+"grp");
					v.setCount(v.getCount() + t.getCount());
					pr.getValues().put(i+"grp", v);
				}
				else {
					pr.getGroups().add(groupBy);
					int i = pr.getGroups().indexOf(groupBy);
					Fraction v = new Fraction(t.getCount(), getTotal(payload.getType(), t, weigth));
					pr.getValues().put(i+"grp", v);
				}
			}
		}
		
		if(payload.getGroupFilters() != null && payload.getGroupFilters().size() > 0){
			return postProcess(this.processFilters(pr,payload.getGroupFilters()));
		}
		else
			return postProcess(pr);
	}
	
	AnalysisPayloadResult postProcess(AnalysisPayloadResult result){
		boolean sameTotal = true;
		int i = -1;
		for(String k : result.getValues().keySet()){
			if(i != -1 && i != result.getValues().get(k).getTotal()){
				sameTotal = false;
				break;
			}
			else {
				i = result.getValues().get(k).getTotal();
			}
		}
		result.setDistribution(sameTotal && result.getType() != _CG.PD && result.getType() != _CG.VD); 
		return result;
	}
	
	public int getTotal(_CG cg, Tray t, int total){
		switch (cg) {
		case PD:
			return t.getWeigth();
		case PT:
			return total;
		case V:
			return total;
		case VD:
			return t.getWeigth();
		case VT:
			return total;
		}
		
		return 0;
	}
	
	
	
	public AnalysisPayloadResult processFilters(AnalysisPayloadResult result, List<Map<Field, String>> filters){
		AnalysisPayloadResult reResult = new AnalysisPayloadResult();
		reResult.setFilters(result.getFilters());
		reResult.setDisplay(result.getDisplay());
		reResult.setType(result.getType());
		
		for(Set<FieldValue> group : result.getGroups()){
			if(pass(group, filters)){
				reResult.getGroups().add(group);
				reResult.getValues().put(reResult.getGroups().indexOf(group)+"grp", result.getValues().get(result.getGroups().indexOf(group)+"grp"));
			}
		}
		
		return reResult;
	}
	
	public boolean pass(Set<FieldValue> group, List<Map<Field, String>> filters){
		return filters.stream().filter(x -> {
			boolean ok = true;
			for(FieldValue v : group){
				ok = ok && ((x.containsKey(v.getField()) && x.get(v.getField()).equals(v.getValue())) || !x.containsKey(v.getField()));
			}
			return ok;
		}).findFirst().isPresent();
	}

}
