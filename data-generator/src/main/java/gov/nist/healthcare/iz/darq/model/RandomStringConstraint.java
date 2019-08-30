package gov.nist.healthcare.iz.darq.model;

public class RandomStringConstraint extends ValueConstraint {
	
	public RandomStringConstraint() {
		super(ValueConstraintType.STRING);
	}
	
	private int minLength;
	private int maxLength;
	
	public int getMinLength() {
		return minLength;
	}
	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}
	public int getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

}
