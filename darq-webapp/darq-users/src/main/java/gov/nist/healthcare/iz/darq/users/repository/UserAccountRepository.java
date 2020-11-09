package gov.nist.healthcare.iz.darq.users.repository;

import gov.nist.healthcare.auth.repository.AccountRepository;
import gov.nist.healthcare.iz.darq.access.domain.UserRole;
import gov.nist.healthcare.iz.darq.users.domain.IssuerId;
import gov.nist.healthcare.iz.darq.users.domain.UserAccount;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends AccountRepository<UserAccount, UserRole> {
    UserAccount findByEmail(String email);
    UserAccount findByIssuerIdListIsContaining(IssuerId issuerId);
}
