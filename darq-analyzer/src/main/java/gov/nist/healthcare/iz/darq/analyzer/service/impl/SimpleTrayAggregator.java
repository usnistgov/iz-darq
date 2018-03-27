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

@Service
public class SimpleTrayAggregator implements TrayAggregator {

	@Override
	public AnalysisPayloadResult aggregate(List<Tray> trays, AnalysisPayload payload) {
		AnalysisPayloadResult pr = new AnalysisPayloadResult();
		pr.setFilters(payload.getFilters());
		for(Tray t : trays){
			Set<FieldValue> groupBy = new HashSet<>();
			for(Field group : payload.getGroupBy()){
				String value = t.get(group);
				groupBy.add(new FieldValue(group, value));
			}
			
			if(pr.getGroups().contains(groupBy)){
				int i = pr.getGroups().indexOf(groupBy);
				double v = pr.getValues().get(i+"grp");
				pr.getValues().put(i+"grp", v+t.getCount());
			}
			else {
				pr.getGroups().add(groupBy);
				int i = pr.getGroups().indexOf(groupBy);
				pr.getValues().put(i+"grp", (double) t.getCount());
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
