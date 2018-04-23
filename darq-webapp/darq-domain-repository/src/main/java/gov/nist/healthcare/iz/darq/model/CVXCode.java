package gov.nist.healthcare.iz.darq.model;

import org.springframework.data.annotation.Id;

public class CVXCode {
	
	@Id
	private String cvx;
	private String name;
	
	public String getCvx() {
		return cvx;
	}
	public void setCvx(String cvx) {
		this.cvx = cvx;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
