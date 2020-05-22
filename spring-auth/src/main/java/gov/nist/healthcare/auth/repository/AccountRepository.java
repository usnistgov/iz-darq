package gov.nist.healthcare.auth.repository;

import gov.nist.healthcare.auth.domain.Privilege;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import gov.nist.healthcare.auth.domain.Account;

import java.util.List;

@Repository
public interface AccountRepository extends MongoRepository<Account, String> {
	
	public Account findByUsername(String username);
	@Query(value="{ 'privileges' : { '$not' : { '$in' : [ { '$ref' :  'privilege', '$id' : ?0 } ] }}}")
	public List<Account> allExceptRole(String id);
	public Account findByEmail(String email);
}