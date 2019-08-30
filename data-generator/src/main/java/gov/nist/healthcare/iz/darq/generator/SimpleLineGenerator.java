package gov.nist.healthcare.iz.darq.generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import gov.nist.healthcare.iz.darq.model.FieldModel;

public class SimpleLineGenerator implements LineGenerator {

	Map<String, String> values = new HashMap<>();
	
	@Override
	public String generate(List<FieldModel> fields, String separator) {
		Map<Boolean, List<FieldModel>> ready = fields.stream().collect(Collectors.partitioningBy(field -> {
			if(field.getDependencies() == null || field.getDependencies().size() == 0) return true;
			else return values.keySet().containsAll(field.getDependencies());
		}));
		return null;
	}
	
	
	public String valueFor(FieldModel model, Map<String, String> values){
		if(model.getValue() != null){
			
		}
		else {
			
		}
	}

}
