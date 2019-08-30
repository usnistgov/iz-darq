package gov.nist.healthcare.iz.darq.generator.constraints;

import java.util.Map;

import gov.nist.healthcare.iz.darq.model.LitteralValue;
import gov.nist.healthcare.iz.darq.model.ReferenceValue;
import gov.nist.healthcare.iz.darq.model.Value;

public class ValueExtraction {
	
	public static String extract(Map<String, String> values, Value v){
		if(v instanceof ReferenceValue){
			return values.get(((ReferenceValue) v).getVarName());
		}
		else if(v instanceof LitteralValue){
			return ((LitteralValue) v).getValue();
		}
		return null;
	}

}
