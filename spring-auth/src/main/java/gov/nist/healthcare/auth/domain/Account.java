package gov.nist.healthcare.auth.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.userdetails.UserDetails;

public class Account<P extends Authority> implements UserDetails {

	@Id
	private String id;
	private String username;
	@JsonIgnore
	private String password;
	private boolean pending = false;
	private boolean locked = false;
	@DBRef(db = "privilege")
	@Field("privileges")
	private Set<P> authorities;
	
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isPending() {
		return pending;
	}
	public void setPending(boolean pending) {
		this.pending = pending;
	}
	public void setAuthorities(Set<P> authorities) {
		this.authorities = authorities;
	}
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	//---- User Details Getters
	@Override
	@Transient
	public boolean isAccountNonExpired() {
		return true;
	}
	@Override
	@Transient
	public boolean isAccountNonLocked() {
		return !this.isLocked();
	}
	@Override
	@Transient
	public boolean isCredentialsNonExpired() {
		return true;
	}
	@Override
	@Transient
	public boolean isEnabled() {
		return !this.isPending();
	}
	@Override
	@Field("privileges")
	public Set<P> getAuthorities() {
		return Collections.unmodifiableSet(authorities != null ? authorities : new HashSet<>());
	}
}
