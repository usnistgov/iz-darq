package gov.nist.healthcare.iz.darq.controller.domain;

public class OpResult {
	
	private boolean status;
	private String message;
	private String op;
	
	
	public OpResult(boolean status, String message, String op) {
		super();
		this.status = status;
		this.message = message;
		this.op = op;
	}
	
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
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
