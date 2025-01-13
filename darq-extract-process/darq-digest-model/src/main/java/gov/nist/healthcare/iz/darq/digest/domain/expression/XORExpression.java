package gov.nist.healthcare.iz.darq.digest.domain.expression;

import java.util.*;
import java.util.stream.Collectors;

public class XORExpression extends Expression {
	List<Expression> expressions;

	public XORExpression() {
		super(ExpressionType.XOR);
	}

	public List<Expression> getExpressions() {
		return expressions;
	}

	public void setExpressions(List<Expression> expressions) {
		this.expressions = expressions;
	}

	@Override
	public String getStringValue() {
		List<String> expressionStrings = expressions.stream()
		                                            .map(Expression::getStringValue)
		                                            .sorted()
		                                            .collect(Collectors.toList());
		return "(" + String.join(" XOR ", expressionStrings) + ")";
	}

	@Override
	public List<String> validate() {
		List<String> errors = new ArrayList<>();
		if(expressions == null || expressions.size() < 2) {
			errors.add("XOR operator shall have at least two expressions");
		} else {
			expressions.forEach((expression) -> {
				if(expression == null) {
					errors.add("XOR operator's operand expression shall not be null");
				} else {
					errors.addAll(expression.validate());
				}
			});
		}
		return errors;
	}

	@Override
	public Set<String> getLeafDetectionCodes() {
		Set<String> codes = new HashSet<>();
		if(expressions != null) {
			expressions.forEach((expression) -> codes.addAll(expression.getLeafDetectionCodes()));
		}
		return codes;
	}
}
