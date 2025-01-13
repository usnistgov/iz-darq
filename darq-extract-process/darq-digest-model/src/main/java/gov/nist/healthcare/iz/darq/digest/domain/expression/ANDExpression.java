package gov.nist.healthcare.iz.darq.digest.domain.expression;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ANDExpression extends Expression {
	List<Expression> expressions;

	public ANDExpression() {
		super(ExpressionType.AND);
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
		return "(" + String.join(" AND ", expressionStrings) + ")";
	}

	@Override
	public List<String> validate() {
		List<String> errors = new ArrayList<>();
		if(expressions == null || expressions.size() < 2) {
			errors.add("AND operator shall have at least two expressions");
		} else {
			expressions.forEach((expression) -> {
				if(expression == null) {
					errors.add("AND operator's operand expression shall not be null");
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
