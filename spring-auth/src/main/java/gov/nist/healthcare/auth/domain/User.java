package gov.nist.healthcare.auth.domain;

import java.util.ArrayList;
import java.util.List;

public class User {
	
	private String id;
	private String username;
	private List<String> authorities;
	
	public User(String id, String username, List<Privilege> privileges) {
		super();
		this.id = id;
		this.username = username;
		this.authorities = new ArrayList<>();
		for(Privilege p : privileges){
			this.authorities.add(p.getRole());
		}
	}
	
	public User() {
		super();
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<String> authorities) {
		this.authorities = authorities;
	}
}
