package gov.nist.healthcare.auth.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.GrantedAuthority;

public interface PrivilegeRepository<P extends GrantedAuthority> extends MongoRepository<P, String> {
	P findByRole(String role);
}