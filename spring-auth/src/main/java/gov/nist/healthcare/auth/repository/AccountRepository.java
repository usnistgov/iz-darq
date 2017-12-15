package gov.nist.healthcare.auth.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gov.nist.healthcare.auth.domain.Account;

@Repository
public interface AccountRepository extends MongoRepository<Account, String> {
	
	public Account findByUsername(String username);
	public Account findByEmail(String email);
}