package gov.nist.healthcare.auth.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import gov.nist.healthcare.auth.domain.Privilege;

public interface PrivilegeRepository extends MongoRepository<Privilege, String> {
	
	public Privilege findByRole(String role);
}