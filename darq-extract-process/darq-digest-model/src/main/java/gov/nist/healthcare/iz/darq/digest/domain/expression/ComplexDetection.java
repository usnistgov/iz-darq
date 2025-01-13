package gov.nist.healthcare.iz.darq.digest.domain.expression;

import java.util.Objects;

public class ComplexDetection {
	String code;
	ComplexDetectionTarget target;
	String description;
	Expression expression;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	public ComplexDetectionTarget getTarget() {
		return target;
	}

	public void setTarget(ComplexDetectionTarget target) {
		this.target = target;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(o == null || getClass() != o.getClass()) {
			return false;
		}
		ComplexDetection that = (ComplexDetection) o;
		return Objects.equals(getCode(), that.getCode()) && getTarget() == that.getTarget() && Objects.equals(
				getExpression(),
				that.getExpression()
		) && Objects.equals(getDescription(), that.getDescription());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getCode(), getTarget(), getExpression(), getDescription());
	}
}
