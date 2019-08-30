package gov.nist.healthcare.iz.darq.model;

public class RandomDateConstraint extends ValueConstraint {
	
	public RandomDateConstraint() {
		super(ValueConstraintType.DATE);
	}
	
	private Value min;
	private Value max;
	
	public Value getMin() {
		return min;
	}
	public void setMin(Value min) {
		this.min = min;
	}
	public Value getMax() {
		return max;
	}
	public void setMax(Value max) {
		this.max = max;
	}
	
}
