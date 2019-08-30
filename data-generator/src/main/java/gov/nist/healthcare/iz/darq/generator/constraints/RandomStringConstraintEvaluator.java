package gov.nist.healthcare.iz.darq.generator.constraints;

import java.util.Map;
import java.util.Random;

import gov.nist.healthcare.iz.darq.model.FieldModel;
import gov.nist.healthcare.iz.darq.model.RandomStringConstraint;

public class RandomStringConstraintEvaluator implements ConstraintEvaluator<RandomStringConstraint> {

	private Random rand = new Random();
	
	@Override
	public String evaluate(FieldModel model, RandomStringConstraint constraint, Map<String, String> values) {
		int n = rand.nextInt(constraint.getMaxLength() - constraint.getMinLength()) + constraint.getMinLength();
		String str = "";
		for(int i = 0; i < n; i++){
			str += "x";
		}
		return str;
	}

	
}
