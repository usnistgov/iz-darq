package gov.nist.healthcare.iz.darq.analyzer.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisPayload;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisPayload.FieldValue;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisPayload.GroupFilter;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisSectionResult.AnalysisPayloadResult;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisSectionResult.Group;
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

		// Calculate total weight of trays
		int weigth = trays.stream().map(x -> {
			return x.getWeigth();
		})
		.reduce(0, (x,y) -> {
			return x + y;
		});

		// Set Payload Metadata
		pr.setFilters(payload.getFilters());
		pr.setDisplay(payload.getOptions());
		pr.setType(payload.getType());
		
		for(Tray t : trays){

			// If there are no groups set as Indicator
			if(payload.getGroupBy() == null || payload.getGroupBy().size() == 0){
				if(pr.getValues().containsKey("indicator")){
					Fraction v = pr.getValues().get("indicator");
					v.setCount(v.getCount() + t.getCount());
					pr.getValues().put("indicator", v);
				}
				else {
					Fraction v = new Fraction(t.getCount(), weigth);
					pr.getValues().put("indicator", v);
				}
			}
			// If there are groups
			else {

				// Create group values
				Set<FieldValue> groupBy = new HashSet<>();
				for(Field group : payload.getGroupBy()){
					String value = t.get(group);
					groupBy.add(new FieldValue(group, value));
				}
				
				// Get group ID
				int index = getIndex(pr.getGroups(), groupBy);

				// If group exists
				if(index != -1){
					int i = index;
					Fraction v = pr.getValues().get(i+"grp");

					// Update count
					v.setCount(v.getCount() + t.getCount());

					// If type is not detection
					if(!useTotal(payload.getType())){
						v.setTotal(v.getTotal() + t.getWeigth());
					}
					pr.getValues().put(i+"grp", v);
				}
				else {
					pr.getGroups().add(new Group(payload.getOptions().getThreshold(),groupBy));
					int i = pr.getGroups().size() - 1;
					Fraction v = new Fraction(t.getCount(), useTotal(payload.getType()) ? weigth : t.getWeigth());
					pr.getValues().put(i+"grp", v);
				}
			}
		}
		if(payload.getGroupBy() != null && payload.getGroupBy().size() > 0 && payload.getGroupFilters() != null && payload.getGroupFilters().size() > 0){
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
	
	public boolean useTotal(_CG cg){
		switch (cg) {
		case PD:
			return false;
		case PT:
			return true;
		case V:
			return true;
		case VD:
			return false;
		case VT:
			return true;
		}
		return false;
	}
	
	public int getIndex(List<Group> groups, Set<FieldValue> fields){
		Optional<Group> grp = groups.stream().filter(x -> x.getFields().equals(fields)).findFirst();
		if(grp.isPresent()){
			return groups.indexOf(grp.get());
		}
		else 
			return -1;
	}
	
	public AnalysisPayloadResult processFilters(AnalysisPayloadResult result, List<GroupFilter> filters){
		AnalysisPayloadResult reResult = new AnalysisPayloadResult();
		reResult.setFilters(result.getFilters());
		reResult.setDisplay(result.getDisplay());
		reResult.setType(result.getType());
		
		for(Group group : result.getGroups()){
			GroupFilter gf = pass(group.getFields(), filters);
			if(gf != null){
				
				reResult.getGroups().add(new Group(gf.getThreshold(),group.getFields()));
				int i1 = getIndex(reResult.getGroups(), group.getFields());
				int i2 = getIndex(result.getGroups(), group.getFields());
				reResult.getValues().put(i1+"grp", result.getValues().get(i2+"grp"));
			}
		}
		return reResult;
	}
	
	public GroupFilter pass(Set<FieldValue> group, List<GroupFilter> filters){
		return filters.stream().filter(x -> {
			boolean ok = true;
			if(x.getValues() != null && x.getValues().size() > 0) {
				for(FieldValue v : group){

					ok = ok && ((x.getValues().containsKey(v.getField()) && x.getValues().get(v.getField()).equals(v.getValue())) || !x.getValues().containsKey(v.getField()));
				}
			}
			return ok;
		}).findFirst().orElseGet(() -> null);
	}

}
