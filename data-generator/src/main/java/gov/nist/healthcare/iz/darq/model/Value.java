package gov.nist.healthcare.iz.darq.model;

public abstract class Value {
	
	private ValueType type;

	public ValueType getType() {
		return type;
	}

	public void setType(ValueType type) {
		this.type = type;
	}
	
	
}
