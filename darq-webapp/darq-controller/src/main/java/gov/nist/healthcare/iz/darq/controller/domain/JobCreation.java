package gov.nist.healthcare.iz.darq.controller.domain;

public class JobCreation {
	private boolean status;
	private String message;
	
	
	public JobCreation(boolean status, String message) {
		super();
		this.status = status;
		this.message = message;
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
	
}
