package gov.nist.healthcare.auth.service;

import gov.nist.healthcare.auth.domain.Authority;
import gov.nist.healthcare.auth.domain.PasswordChangeRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import gov.nist.healthcare.auth.domain.Account;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Set;

public interface AccountService<T extends Account<E>, E extends Authority> extends UserDetailsService {

	List<T> getAllUsers();
	T getAccountByUsername(String username);
	T getAccountById(String id);
	T changePassword(PasswordChangeRequest change) throws BadCredentialsException, UsernameNotFoundException;
	T save(T account);
	T create(T account);
	T setLock(String account, boolean lock) throws UsernameNotFoundException;
	T approve(String account) throws UsernameNotFoundException;
	T grantPrivilege(String account, String role) throws IllegalArgumentException, UsernameNotFoundException;
	T setPrivilege(String account, String role) throws IllegalArgumentException, UsernameNotFoundException;
	T revokePrivilege(String account, String role) throws IllegalArgumentException, UsernameNotFoundException;
	E getPrivilegeByRole(String role);
	E createPrivilegeByRole(E privilege);
	Set<E> getAllPrivileges();
	boolean exists(String username);

}