package gov.nist.healthcare.iz.darq.data.service;

import java.io.InputStream;
import java.util.UUID;

public class JobDataToken {
	
	private String userId;
	private String mount;
	private InputStream patients;
	private InputStream vaccines;

	
	public JobDataToken(String userId, InputStream patients, InputStream vaccines) {
		super();
		this.userId = userId;
		this.mount = UUID.randomUUID().toString();
		this.patients = patients;
		this.vaccines = vaccines;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getMount() {
		return mount;
	}

	public void setMount(String mount) {
		this.mount = mount;
	}

	public InputStream getPatients() {
		return patients;
	}

	public void setPatients(InputStream patients) {
		this.patients = patients;
	}

	public InputStream getVaccines() {
		return vaccines;
	}

	public void setVaccines(InputStream vaccines) {
		this.vaccines = vaccines;
	}

}
