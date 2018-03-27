package gov.nist.healthcare.iz.darq.controller.domain;

public class OpAck {
	
	public enum AckStatus {
		SUCCESS, FAILURE, WARNING, INFORM
	}
	
	private AckStatus status;
	private String message;
	private String op;
	
	public OpAck(AckStatus status, String message, String op) {
		super();
		this.status = status;
		this.message = message;
		this.op = op;
	}

	public AckStatus getStatus() {
		return status;
	}

	public void setStatus(AckStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}
	
}
