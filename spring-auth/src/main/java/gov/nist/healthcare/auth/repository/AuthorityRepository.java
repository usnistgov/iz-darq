package gov.nist.healthcare.auth.repository;

import gov.nist.healthcare.auth.domain.Authority;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuthorityRepository<P extends Authority> extends MongoRepository<P, String> {
    P findByRole(String role);
}