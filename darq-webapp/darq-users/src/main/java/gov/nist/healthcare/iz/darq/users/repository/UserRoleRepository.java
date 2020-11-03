package gov.nist.healthcare.iz.darq.users.repository;

import gov.nist.healthcare.auth.repository.AuthorityRepository;
import gov.nist.healthcare.iz.darq.access.domain.UserRole;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends AuthorityRepository<UserRole> {
}
