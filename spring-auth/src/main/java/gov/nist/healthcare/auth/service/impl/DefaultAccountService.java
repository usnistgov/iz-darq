package gov.nist.healthcare.auth.service.impl;

import java.util.*;

import gov.nist.healthcare.auth.domain.Authority;
import gov.nist.healthcare.auth.domain.PasswordChangeRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.repository.AccountRepository;
import gov.nist.healthcare.auth.repository.AuthorityRepository;

public class DefaultAccountService<T extends Account<P>, P extends Authority> implements gov.nist.healthcare.auth.service.AccountService<T, P> {

	private final AccountRepository<T, P> accountRepository;
	private final AuthorityRepository<P> authorityRepository;
	private final PasswordEncoder encoder;

	public DefaultAccountService(AccountRepository<T, P> accountRepository, AuthorityRepository<P> authorityRepository, PasswordEncoder encoder) {
		this.accountRepository = accountRepository;
		this.authorityRepository = authorityRepository;
		this.encoder = encoder;
	}

	@Override
	public T getAccountByUsername(String username) {
		return accountRepository.findByUsernameIgnoreCase(username);
	}
	@Override
	public T getAccountById(String userId) {
		return accountRepository.findOne(userId);
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
		if ((account.getUsername() != null && !account.getUsername().isEmpty()) || (account.getPassword() != null && !account.getPassword().isEmpty())) {
			T existing = this.getAccountByUsername(account.getUsername());
			if (existing != null && !existing.getId().equals(account.getId())) {
				throw new IllegalArgumentException("Cannot save user, username already exists");
			}
		}
		return this.accountRepository.save(account);
	}

	@Override
	public T create(T account) {
		account.setPassword(this.encoder.encode(account.getPassword()));
		return this.save(account);
	}

	@Override
	public T setLock(String id, boolean lock) {
		T account = this.getAccountByIdOrFail(id);
		account.setLocked(lock);
		this.save(account);
		return account;
	}

	@Override
	public T approve(String id) {
		T account = this.getAccountByIdOrFail(id);
		account.setPending(false);
		this.save(account);
		return account;
	}

	@Override
	public T setPrivilege(String id, String role) throws IllegalArgumentException, UsernameNotFoundException {
		T account = this.getAccountByIdOrFail(id);
		P privilege = this.getAuthorityByRoleOrFail(role);

		account.setAuthorities(Collections.singleton(privilege));
		this.save(account);
		return account;
	}

	@Override
	public T grantPrivilege(String id, String role) throws IllegalArgumentException, UsernameNotFoundException {
		T account = this.getAccountByIdOrFail(id);
		P privilege = this.getAuthorityByRoleOrFail(role);

		account.getAuthorities().add(privilege);
		this.save(account);
		return account;
	}

	@Override
	public T revokePrivilege(String id, String role) throws IllegalArgumentException, UsernameNotFoundException {
		T account = this.getAccountByIdOrFail(id);
		Optional<P> privilege = account.getAuthorities().stream().filter((p) -> p.getAuthority().equals(role)).findAny();
		if(privilege.isPresent()) {
			account.getAuthorities().remove(privilege.get());
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

	T getAccountByIdOrFail(String id) throws UsernameNotFoundException {
		T account = this.getAccountById(id);
		if(account == null) {
			throw new UsernameNotFoundException(id);
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
		return this.authorityRepository.findByRole(role);
	}

	@Override
	public P createPrivilegeByRole(P privilege) {
		P existing = this.authorityRepository.findByRole(privilege.getAuthority());
		if(existing == null) {
			return this.authorityRepository.save(privilege);
		}
		return existing;
	}

	@Override
	public Set<P> getAllPrivileges() {
		return new HashSet<>(this.authorityRepository.findAll());
	}

	@Override
	public boolean exists(String username) {
		return this.accountRepository.existsByUsernameIgnoreCase(username);
	}

}