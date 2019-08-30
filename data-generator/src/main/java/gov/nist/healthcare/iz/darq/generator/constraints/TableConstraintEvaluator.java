package gov.nist.healthcare.iz.darq.generator.constraints;

import java.util.Map;

import gov.nist.healthcare.iz.darq.model.FieldModel;
import gov.nist.healthcare.iz.darq.model.Table;
import gov.nist.healthcare.iz.darq.model.TableConstraint;

public class TableConstraintEvaluator implements ConstraintEvaluator<TableConstraint> {

	Map<String, Table> tables;
	
	@Override
	public String evaluate(FieldModel model, TableConstraint constraint, Map<String, String> values) {
		return tables.get(constraint.getTable()).getValueUsing(constraint.getCodes());
	}


}
