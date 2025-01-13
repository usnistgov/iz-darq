package gov.nist.healthcare.iz.darq.digest.domain.expression;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = ANDExpression.class, name = "AND"),
		@JsonSubTypes.Type(value = ORExpression.class, name = "OR"),
		@JsonSubTypes.Type(value = XORExpression.class, name = "XOR"),
		@JsonSubTypes.Type(value = NOTExpression.class, name = "NOT"),
		@JsonSubTypes.Type(value = IMPLYExpression.class, name = "IMPLY"),
		@JsonSubTypes.Type(value = DetectionExpression.class, name = "DETECTION"),
})
public abstract class Expression {
	protected final ExpressionType type;

	public Expression(ExpressionType type) {
		this.type = type;
	}

	@JsonIgnore
	public abstract String getStringValue();

	public abstract List<String> validate();
	@JsonIgnore
	public abstract Set<String> getLeafDetectionCodes();

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(o == null || getClass() != o.getClass()) {
			return false;
		}
		Expression that = (Expression) o;
		return type == that.type && Objects.equals(getStringValue(), that.getStringValue());
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, getStringValue());
	}
}
