package gov.nist.healthcare.iz.darq.generator.constraints;

import java.util.Map;

import gov.nist.healthcare.iz.darq.model.Condition;
import gov.nist.healthcare.iz.darq.model.ConditionalConstraint;
import gov.nist.healthcare.iz.darq.model.FieldModel;

public class ConditionalConstraintEvaluator implements ConstraintEvaluator<ConditionalConstraint> {

	DefaultConstraintEvaluator constraintEvaluator;
	
	@Override
	public String evaluate(FieldModel model, ConditionalConstraint constraint, Map<String, String> values) {
		if(checkCondition(values, constraint.getCondition())){
			return this.constraintEvaluator.eval(constraint.getSuccess(), model, values);
		}
		else {
			return this.constraintEvaluator.eval(constraint.getFailure(), model, values);
		}
	}
	
	public boolean checkCondition(Map<String, String> values, Condition condition){
		return true;
	}
	
	
//	public boolean compare(String v1, String v2, )

}
