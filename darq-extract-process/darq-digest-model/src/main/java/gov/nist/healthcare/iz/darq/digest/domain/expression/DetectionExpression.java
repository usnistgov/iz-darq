package gov.nist.healthcare.iz.darq.digest.domain.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class DetectionExpression extends Expression {
	String code;

	public DetectionExpression() {
		super(ExpressionType.DETECTION);
	}

	public DetectionExpression(String code) {
		super(ExpressionType.DETECTION);
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String getStringValue() {
		return code;
	}

	@Override
	public List<String> validate() {
		List<String> errors = new ArrayList<>();
		if(code == null || code.isEmpty()) {
			errors.add("Detection Expression shall have a detection code");
		}
		return errors;
	}

	@Override
	public Set<String> getLeafDetectionCodes() {
		return Collections.singleton(code);
	}


}
