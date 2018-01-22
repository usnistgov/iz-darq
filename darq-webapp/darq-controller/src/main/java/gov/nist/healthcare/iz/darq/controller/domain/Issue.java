package gov.nist.healthcare.iz.darq.controller.domain;

public class Issue {
	private String code;
	private String message;
	private String location;
	public Issue(String code, String message, String location) {
		super();
		this.code = code;
		this.message = message;
		this.location = location;
	}
	public Issue() {
		super();
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	
}
