package gov.nist.healthcare.iz.darq.generator.constraints;

import java.util.Map;

import gov.nist.healthcare.iz.darq.model.FieldModel;
import gov.nist.healthcare.iz.darq.model.ValueConstraint;

public class DefaultConstraintEvaluator {

	private Map<Class<? extends ValueConstraint>, ConstraintEvaluator> evaluators;
	
	public <T extends ValueConstraint> String  eval(T constraint, FieldModel model, Map<String, String> values){
		if(this.evaluators.containsKey(constraint.getClass())){
			return this.evaluators.get(constraint.getClass()).evaluate(model, constraint, values);
		}
		return "";
	}
	
	
	
}
