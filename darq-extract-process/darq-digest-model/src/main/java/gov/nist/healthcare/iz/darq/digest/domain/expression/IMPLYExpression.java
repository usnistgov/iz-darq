package gov.nist.healthcare.iz.darq.digest.domain.expression;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IMPLYExpression extends Expression {
	Expression left;
	Expression right;

	public IMPLYExpression() {
		super(ExpressionType.IMPLY);
	}

	public Expression getLeft() {
		return left;
	}

	public void setLeft(Expression left) {
		this.left = left;
	}

	public Expression getRight() {
		return right;
	}

	public void setRight(Expression right) {
		this.right = right;
	}

	@Override
	public String getStringValue() {
		return "(" + left.getStringValue() + " IMPLIES " + right.getStringValue() + ")";
	}

	@Override
	public List<String> validate() {
		List<String> errors = new ArrayList<>();
		if(left == null) {
			errors.add("IMPLY Expression left operand can't be empty");
		} else {
			errors.addAll(left.validate());
		}
		if(right == null) {
			errors.add("IMPLY Expression right operand can't be empty");
		} else {
			errors.addAll(right.validate());
		}
		return errors;
	}

	@Override
	public Set<String> getLeafDetectionCodes() {
		Set<String> codes = new HashSet<>();
		if(left != null) {
			codes.addAll(left.getLeafDetectionCodes());
		}
		if(right != null) {
			codes.addAll(right.getLeafDetectionCodes());
		}
		return codes;
	}
}
