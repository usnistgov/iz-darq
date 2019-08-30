package gov.nist.healthcare.iz.darq.model;

public class ConstantConstraint extends ValueConstraint {
	
	public ConstantConstraint() {
		super(ValueConstraintType.CONSTANT);
	}

	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
