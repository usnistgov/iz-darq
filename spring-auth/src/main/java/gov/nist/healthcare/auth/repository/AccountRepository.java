package gov.nist.healthcare.auth.repository;

import gov.nist.healthcare.auth.domain.Authority;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import gov.nist.healthcare.auth.domain.Account;

import java.util.List;


public interface AccountRepository<T extends Account<E>, E extends Authority> extends MongoRepository<T, String> {
	
	T findByUsername(String username);
	T findById(String id);
	@Query(value="{ 'authorities' : { '$not' : { '$in' : [ { '$ref' :  'privilege', '$id' : ?0 } ] }}}")
	List<T> allExceptRole(String id);
	boolean existsByUsername(String username);
}