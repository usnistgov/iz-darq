package gov.nist.healthcare.iz.darq.model;

import java.util.List;
import java.util.Random;
import java.util.Map.Entry;

public class Table {

	private String name;
	private List<String> values;
	private Random rand = new Random();
	
	public String getValueUsing(Distribution distribution){
		if(distribution == null || distribution.getCodeProbability() == null || distribution.getCodeProbability().keySet().size() == 0){
			int n = rand.nextInt(values.size()-1);
			return this.values.get(n);
		}
		else {
			int n = rand.nextInt(100);
			Double cumul = 0.0;
			String code = "";
			int i = 0;
			for(Entry<String, Double> entry : distribution.getCodeProbability().entrySet()){
				cumul +=  entry.getValue();
				code = entry.getKey();
				
				if(cumul >= n){
					break;
				}
			}
			
			
			if(cumul >= n){
				n = rand.nextInt(values.size()-1);
				return this.values.get(n);
			}
			else {
				return code;
			}
			
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public Random getRand() {
		return rand;
	}

	public void setRand(Random rand) {
		this.rand = rand;
	}
	
	
}
