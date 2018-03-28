package gov.nist.healthcare.iz.darq.analyzer.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisPayload;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisPayload.FieldValue;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisSectionResult.AnalysisPayloadResult;
import gov.nist.healthcare.iz.darq.analyzer.domain.Field;
import gov.nist.healthcare.iz.darq.analyzer.domain.Tray;
import gov.nist.healthcare.iz.darq.analyzer.service.TrayAggregator;
import gov.nist.healthcare.iz.darq.digest.domain.Fraction;

@Service
public class SimpleTrayAggregator implements TrayAggregator {

	@Override
	public AnalysisPayloadResult aggregate(List<Tray> trays, AnalysisPayload payload) {
//		System.out.println(trays.size());
		AnalysisPayloadResult pr = new AnalysisPayloadResult();
		int weigth = trays.stream().map(x -> {
			return x.getWeigth();
		})
		.reduce(0, (x,y) -> {
			return x + y;
		});
		pr.setFilters(payload.getFilters());
		for(Tray t : trays){
			
			if(payload.getGroupBy() == null || payload.getGroupBy().size() == 0){
				if(pr.getValues().containsKey("indicator")){
					Fraction v = pr.getValues().get("indicator");
					v.setCount(v.getCount()+t.getCount());
					pr.getValues().put("indicator", v);
				}
				else {
					pr.getValues().put("indicator", new Fraction(t.getCount(), weigth));
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
					v.setCount(v.getCount()+t.getCount());
					pr.getValues().put(i+"grp", v);
				}
				else {
					pr.getGroups().add(groupBy);
					int i = pr.getGroups().indexOf(groupBy);
					pr.getValues().put(i+"grp", new Fraction(t.getCount(), weigth));
				}
			}
			
		}
		return pr;
	}
	
	public static void main(String[] args) {
		Set<String> x = new HashSet<>();
		x.add("A");
		x.add("B");
		Set<String> y = new HashSet<>();
		y.add("B");
		y.add("A");
		System.out.println(x.containsAll(y));
	}

}
