package gov.nist.healthcare.auth.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.domain.Privilege;

public interface AccountService extends UserDetailsService {

	public Account getCurrentUser();
	public Account getAccountByUsername(String username);
	public Account createAdmin(Account account);
	public Account createTester(Account account);
	public Account createUser(Account account,Privilege p);
	public Privilege getPrivilegeByRole(String role);
	public Privilege createPrivilegeByRole(String role);
	public void deleteAll();
	
	
}