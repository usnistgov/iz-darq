package gov.nist.healthcare.iz.darq.generator.constraints;

import java.util.Map;

import gov.nist.healthcare.iz.darq.model.FieldModel;
import gov.nist.healthcare.iz.darq.model.ValueConstraint;

public interface ConstraintEvaluator<T extends ValueConstraint> {

	public String evaluate(FieldModel model, T constraint, Map<String, String> values);
	
}
