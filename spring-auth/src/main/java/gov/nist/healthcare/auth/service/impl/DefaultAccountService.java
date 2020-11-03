package gov.nist.healthcare.auth.service.impl;

import java.util.*;

import gov.nist.healthcare.auth.domain.PasswordChangeRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.repository.AccountRepository;
import gov.nist.healthcare.auth.repository.PrivilegeRepository;
import gov.nist.healthcare.auth.service.AccountService;

public class AccountService<T extends Account<P>, P extends GrantedAuthority> implements gov.nist.healthcare.auth.service.AccountService<T, P> {

	private final AccountRepository<T, P> accountRepository;
	private final PrivilegeRepository<P> privilegeRepository;
	private final PasswordEncoder encoder;

	public AccountService(AccountRepository<T, P> accountRepository, PrivilegeRepository<P> privilegeRepository, PasswordEncoder encoder) {
		this.accountRepository = accountRepository;
		this.privilegeRepository = privilegeRepository;
		this.encoder = encoder;
	}

	@Override
	public T getAccountByUsername(String username) {
		return accountRepository.findByUsername(username);
	}

	@Override
	public T changePassword(PasswordChangeRequest change) throws BadCredentialsException, UsernameNotFoundException {
		T account = this.getAccountByUsernameOrFail(change.getUsername());
		if(this.encoder.matches(change.password, account.getPassword())) {
			account.setPassword(this.encoder.encode(change.getReplacement()));
			return this.save(account);
		} else {
			throw new BadCredentialsException("Incorrect password");
		}
	}

	@Override
	public List<T> getAllUsers() {
		return this.accountRepository.findAll();
	}

	@Override
	public T save(T account) throws IllegalArgumentException {
		T existing = this.getAccountByUsername(account.getUsername());
		if(existing != null && !existing.getId().equals(account.getId())) {
			throw new IllegalArgumentException("Cannot save user, username already exists");
		}
		return this.accountRepository.save(account);
	}

	@Override
	public T create(T account) {
		account.setPassword(this.encoder.encode(account.getPassword()));
		return this.save(account);
	}

	@Override
	public T setLock(String username, boolean lock) {
		T account = this.getAccountByUsernameOrFail(username);
		account.setLocked(lock);
		this.save(account);
		return account;
	}

	@Override
	public T approve(String username) {
		T account = this.getAccountByUsernameOrFail(username);
		account.setPending(false);
		this.save(account);
		return account;
	}

	@Override
	public T grantPrivilege(String username, String role) throws IllegalArgumentException, UsernameNotFoundException {
		T account = this.getAccountByUsernameOrFail(username);
		P privilege = this.getAuthorityByRoleOrFail(role);

		account.getPrivileges().add(privilege);
		this.save(account);
		return account;
	}

	@Override
	public T revokePrivilege(String username, String role) throws IllegalArgumentException, UsernameNotFoundException {
		T account = this.getAccountByUsernameOrFail(username);
		Optional<P> privilege = account.getPrivileges().stream().filter((p) -> p.getAuthority().equals(role)).findAny();
		if(privilege.isPresent()) {
			account.getPrivileges().remove(privilege.get());
			this.save(account);
			return account;
		} else {
			throw new IllegalArgumentException("Privilege " + role + " not found");
		}
	}

	T getAccountByUsernameOrFail(String username) throws UsernameNotFoundException {
		T account = this.getAccountByUsername(username);
		if(account == null) {
			throw new UsernameNotFoundException(username);
		} else {
			return account;
		}
	}

	P getAuthorityByRoleOrFail(String role) throws IllegalArgumentException {
		P privilege = this.getPrivilegeByRole(role);
		if(privilege == null) {
			throw new IllegalArgumentException("Privilege " + role + " not found");
		}
		return privilege;
	}

	@Override
	public T loadUserByUsername(String username) throws UsernameNotFoundException {
		return this.getAccountByUsernameOrFail(username);
	}

	@Override
	public P getPrivilegeByRole(String role) {
		return this.privilegeRepository.findByRole(role);
	}
	
	@Override
	public P createPrivilegeByRole(P privilege) {
		P existing = this.privilegeRepository.findByRole(privilege.getAuthority());
		if(existing == null) {
			return this.privilegeRepository.save(privilege);
		}
		return existing;
	}

	@Override
	public Set<P> getAllPrivileges() {
		return new HashSet<>(this.privilegeRepository.findAll());
	}

	@Override
	public boolean exists(String username) {
		return this.accountRepository.existsByUsername(username);
	}

}