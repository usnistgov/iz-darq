package gov.nist.healthcare.iz.darq.model;

public class ConditionalConstraint extends ValueConstraint {

	public ConditionalConstraint() {
		super(ValueConstraintType.CONDITIONAL);
	}
	
	private Condition condition;
	private ValueConstraint success;
	private ValueConstraint failure;
	
	public Condition getCondition() {
		return condition;
	}
	public void setCondition(Condition condition) {
		this.condition = condition;
	}
	public ValueConstraint getSuccess() {
		return success;
	}
	public void setSuccess(ValueConstraint success) {
		this.success = success;
	}
	public ValueConstraint getFailure() {
		return failure;
	}
	public void setFailure(ValueConstraint failure) {
		this.failure = failure;
	}
	
	
}
