package gov.nist.healthcare.iz.darq.digest.domain.expression;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NOTExpression extends Expression {
	Expression expression;

	public NOTExpression() {
		super(ExpressionType.NOT);
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	@Override
	public String getStringValue() {
		return "(NOT " + expression.getStringValue() + ")";
	}

	@Override
	public List<String> validate() {
		List<String> errors = new ArrayList<>();
		if(expression == null) {
			errors.add("NOT Expression operand can't be empty");
		} else {
			errors.addAll(expression.validate());
		}
		return errors;
	}

	@Override
	public Set<String> getLeafDetectionCodes() {
		Set<String> codes = new HashSet<>();
		if(expression != null) {
			codes.addAll(expression.getLeafDetectionCodes());
		}
		return codes;
	}
}
