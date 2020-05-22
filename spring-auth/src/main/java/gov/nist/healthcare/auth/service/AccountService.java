package gov.nist.healthcare.auth.service;

import gov.nist.healthcare.auth.domain.PasswordChange;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.domain.Privilege;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface AccountService extends UserDetailsService {

	public boolean isAdmin(String user);
	public List<Account> getAllUsersExceptAdmins();
	public Account getCurrentUser();
	public Account getAccountByUsername(String username);
	public Account createAdmin(Account account);
	public Account createTester(Account account);
	public Account changePassword(PasswordChange change) throws BadCredentialsException, UsernameNotFoundException;
	public Account createUser(Account account,Privilege p);
	public Account save(Account account);
	public Privilege getPrivilegeByRole(String role);
	public Privilege createPrivilegeByRole(String role);
	public void deleteAll();
	
	
}