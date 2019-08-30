package gov.nist.healthcare.iz.darq.model;

import java.util.Map;
import java.util.Map.Entry;

public class Distribution {

	private Map<String, Double> codeProbability;

	public Map<String, Double> getCodeProbability() {
		return codeProbability;
	}

	public void setCodeProbability(Map<String, Double> codeProbability) {
		this.codeProbability = codeProbability;
	}

	public Double put(String key, Double value) {
		return codeProbability.put(key, value);
	}

	public Double get(String key) {
		return codeProbability.get(key);
	}
	
	public Double total(){
		Double i = 0.0;
		for(Entry<String, Double> entry : codeProbability.entrySet()){
			i += entry.getValue();
		}
		return i;
	}
	
}
