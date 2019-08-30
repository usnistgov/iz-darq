package gov.nist.healthcare.iz.darq.model;

public class Condition {

	private Value v1;
	private Operator op;
	private Value v2;
	
	public Value getV1() {
		return v1;
	}
	public void setV1(Value v1) {
		this.v1 = v1;
	}
	public Operator getOp() {
		return op;
	}
	public void setOp(Operator op) {
		this.op = op;
	}
	public Value getV2() {
		return v2;
	}
	public void setV2(Value v2) {
		this.v2 = v2;
	}
	
}
