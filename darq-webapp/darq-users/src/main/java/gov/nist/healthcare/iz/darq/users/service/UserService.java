package gov.nist.healthcare.iz.darq.users.service;

import gov.nist.healthcare.auth.domain.PasswordChangeRequest;
import gov.nist.healthcare.iz.darq.access.domain.CreateCredentialsRequest;
import gov.nist.healthcare.iz.darq.access.domain.UserRole;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import gov.nist.healthcare.iz.darq.users.domain.*;
import gov.nist.healthcare.iz.darq.users.exception.FieldValidationException;
import gov.nist.healthcare.iz.darq.users.exception.RequestValidationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Set;

public interface UserService {
    Set<User> getAllUsers();
    User fromAccount(UserAccount account);
    User createAdmin(String username, String password);
    User registerAccount(RegistrationRequest registration) throws FieldValidationException, RequestValidationException;
    boolean existsById(String id);
    UserAccount registerRemoteAccount(RemoteAccountCreationRequest registration) throws RequestValidationException;
    User setLock(String username, boolean lock);
    User approve(String username);
    User revokePrivilege(String username, String role);
    User grantPrivilege(String username, String role);
    User setPrivilege(String username, String role);
    User changePassword(PasswordChangeRequest passwordChangeRequest) throws FieldValidationException;
    User createCredentials(CreateCredentialsRequest createCredentialsRequest) throws UsernameNotFoundException, FieldValidationException, RequestValidationException, OperationFailureException;
    User updateProfile(ProfileUpdateRequest updateRequest, User user) throws UsernameNotFoundException, FieldValidationException;

    UserAccount internalUpdate(ProfileUpdateRequest updateRequest, UserAccount account) throws UsernameNotFoundException, FieldValidationException;

    User changePassword(UserAccount account, String password) throws FieldValidationException;
    User findUserByUsername(String username);
    User findUserByIssuer(String issuer, String issuerId);
    UserAccount findUserAccountByIssuer(String issuer, String issuerId);
    User findUserByIdOrFail(String userId) throws NotFoundException;
    User findUserById(String userId);
    String getUserDisplayName(String userId);
    Set<UserRole> createPrivilegeFromRole(Set<String> roles);
    User getAuthenticationPrincipalAs();
    boolean exists(String username);
    boolean existsOrFail(String username);
    boolean authorizeActionOn(String username, boolean privileged);
}
