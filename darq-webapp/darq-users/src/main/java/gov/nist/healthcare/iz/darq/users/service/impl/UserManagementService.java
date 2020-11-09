package gov.nist.healthcare.iz.darq.users.service.impl;

import com.google.common.base.Strings;
import gov.nist.healthcare.auth.domain.Authority;
import gov.nist.healthcare.auth.domain.PasswordChangeRequest;
import gov.nist.healthcare.auth.service.AccountService;
import gov.nist.healthcare.iz.darq.access.domain.CreateCredentialsRequest;
import gov.nist.healthcare.iz.darq.access.domain.UserRole;
import gov.nist.healthcare.iz.darq.access.domain.UserPermission;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import gov.nist.healthcare.iz.darq.users.domain.*;
import gov.nist.healthcare.iz.darq.users.exception.FieldValidationException;
import gov.nist.healthcare.iz.darq.users.exception.RequestValidationException;
import gov.nist.healthcare.iz.darq.users.repository.UserAccountRepository;
import gov.nist.healthcare.iz.darq.users.service.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.util.stream.Collectors;

public class UserManagementService implements UserService {

    private final AccountService<UserAccount, UserRole> accountService;
    private final String ADMIN_PRIVILEGE;
    private final String USER_PRIVILEGE;
    private final UserAccountRepository accountRepository;
    private final PasswordEncoder encoder;

    public UserManagementService(AccountService<UserAccount, UserRole> accountService, UserAccountRepository  accountRepository, PasswordEncoder encoder, String ADMIN_PRIVILEGE, String USER_PRIVILEGE) {
        this.accountService = accountService;
        this.ADMIN_PRIVILEGE = ADMIN_PRIVILEGE;
        this.USER_PRIVILEGE = USER_PRIVILEGE;
        this.encoder = encoder;
        this.accountRepository = accountRepository;
    }

    public UserAccount findAccountById(String id) {
        return this.accountRepository.findById(id);
    }
    public UserAccount findAccountByEmail(String email) {
        return this.accountRepository.findByEmail(email);
    }

    @Override
    public Set<User> getAllUsers() {
        return this.accountService.getAllUsers().stream().map(this::fromAccount).collect(Collectors.toSet());
    }

    @Override
    public User fromAccount(UserAccount account) {
        User user = new User();
        user.setId(account.getId());
        user.setUsername(account.getUsername());
        user.setName(account.getFullName());
        user.setEmail(account.getEmail());
        user.setOrganization(account.getOrganization());
        if(account.isLocked()) {
            user.setStatus(AccountStatus.LOCKED);
        } else if(account.isPending()) {
            user.setStatus(AccountStatus.PENDING);
        } else {
            user.setStatus(AccountStatus.ACTIVE);
        }
        UserPermission permission = new UserPermission();
        account.getAuthorities().stream().flatMap((role) -> role.getPermissions().stream()).forEach(p -> {
            permission.putGlobalPermission(p , user);
        });
        user.setPermissions(permission);
        user.setCredentials(account.isqDarAccount());
        user.setSource(account.getSource());
        user.setScreenName(this.getUserDisplayName(account));
        user.setRoles(account.getAuthorities().stream().map(Authority::getAuthority).collect(Collectors.toSet()));
        user.setAdministrator(user.getRoles().stream().anyMatch(role -> role.equals(ADMIN_PRIVILEGE)));
        user.setVerified(account.isVerified());
        user.setIssuerIdList(account.getIssuerIdList());
        return user;
    }

    @Override
    public User createAdmin(String username, String password) {
        UserAccount account = new UserAccount();
        account.setUsername(username);
        account.setPassword(password);
        account.setOrganization("QDAR");
        account.setAuthorities(this.accountService.getAllPrivileges());
        return this.fromAccount(this.accountService.create(account));
    }

    @Override
    public User registerAccount(RegistrationRequest registration) throws RequestValidationException {
        List<FieldValidation> validationList = Arrays.asList(
                this.validateUsername(registration.getUsername()),
                this.validatePassword(registration.getPassword()),
                this.validateEmail(registration.getEmail(), null),
                this.validateString("full-name", registration.getFullName()),
                this.validateString("organization", registration.getOrganization())
        );

        if(validationList.stream().map(FieldValidation::isStatus).anyMatch((s) -> !s)) {
            throw new RequestValidationException(validationList.stream().filter((v) -> !v.isStatus()).collect(Collectors.toList()));
        } else {
            UserAccount account = registration.toAccount();
            account.setVerified(false);
            account.setAuthorities(new HashSet<>(Collections.singletonList(this.accountService.getPrivilegeByRole(USER_PRIVILEGE))));
            return this.fromAccount(this.accountService.create(account));
        }
    }

    @Override
    public boolean existsById(String id) {
        return this.accountRepository.exists(id);
    }

    @Override
    public UserAccount registerRemoteAccount(RemoteAccountCreationRequest registration) throws RequestValidationException {
        List<FieldValidation> validationList = Arrays.asList(
                this.validateString("issuer", registration.getIssuer()),
                this.validateString("issuerId", registration.getIssuerId()),
                this.validateEmail(registration.getEmail(), null),
                this.validateString("full-name", registration.getFullName()),
                this.validateString("organization", registration.getOrganization())
        );

        if(validationList.stream().map(FieldValidation::isStatus).anyMatch((s) -> !s)) {
            throw new RequestValidationException(validationList.stream().filter((v) -> !v.isStatus()).collect(Collectors.toList()));
        } else {
            UserAccount account = registration.toAccount();
            account.setVerified(true);
            account.setAuthorities(new HashSet<>(Collections.singletonList(this.accountService.getPrivilegeByRole(USER_PRIVILEGE))));
            return this.accountService.save(account);
        }
    }

    public UserAccount passwordIsMatch(String username, String password) throws NotFoundException {
        UserAccount account = this.accountRepository.findByUsername(username);
        if(account == null) {
            throw new NotFoundException("Wrong username");
        }

        if(!this.encoder.matches(password, account.getPassword())) {
            throw new NotFoundException("Wrong password");
        }

        return account;
    }

    @Override
    public User setLock(String id, boolean lock) throws UsernameNotFoundException, AccessDeniedException {
        this.authorizeActionOn(id, true);
        return this.fromAccount(this.accountService.setLock(id, lock));
    }

    @Override
    public User approve(String id) throws UsernameNotFoundException, AccessDeniedException {
        this.authorizeActionOn(id, true);
        return this.fromAccount(this.accountService.approve(id));
    }

    @Override
    public User revokePrivilege(String id, String role) throws UsernameNotFoundException, AccessDeniedException, IllegalArgumentException {
        this.authorizeActionOn(id, true);
        return this.fromAccount(this.accountService.revokePrivilege(id, role));
    }

    @Override
    public User setPrivilege(String id, String role) throws UsernameNotFoundException, AccessDeniedException, IllegalArgumentException {
        this.authorizeActionOn(id, true);
        return this.fromAccount(this.accountService.setPrivilege(id, role));
    }

    @Override
    public User grantPrivilege(String id, String role) throws UsernameNotFoundException, AccessDeniedException, IllegalArgumentException  {
        this.authorizeActionOn(id, true);
        return this.fromAccount(this.accountService.grantPrivilege(id, role));
    }

    @Override
    public User changePassword(PasswordChangeRequest passwordChangeRequest) throws BadCredentialsException, UsernameNotFoundException, FieldValidationException {
        this.authorizeActionOn(passwordChangeRequest.getUsername(), false);
        this.throwOnValidationFail(this.validatePassword(passwordChangeRequest.getPassword()));
        return this.fromAccount(this.accountService.changePassword(passwordChangeRequest));
    }

    @Override
    public User createCredentials(CreateCredentialsRequest createCredentialsRequest) throws UsernameNotFoundException, RequestValidationException, OperationFailureException {
        this.authorizeActionOn(createCredentialsRequest.getId(), false);
        UserAccount account = this.accountService.getAccountById(createCredentialsRequest.getId());
        if(account == null) {
            throw new UsernameNotFoundException(createCredentialsRequest.getId());
        }

        if(!Strings.isNullOrEmpty(account.getPassword()) || !Strings.isNullOrEmpty(account.getUsername())) {
            throw new OperationFailureException("Credentials for " + account.getUsername() + " already set");
        }

        List<FieldValidation> validationList = Arrays.asList(
                this.validateUsername(createCredentialsRequest.getUsername()),
                this.validatePassword(createCredentialsRequest.getPassword())
        );

        if(validationList.stream().map(FieldValidation::isStatus).anyMatch((s) -> !s)) {
            throw new RequestValidationException(validationList.stream().filter((v) -> !v.isStatus()).collect(Collectors.toList()));
        } else {
            account.setqDarAccount(true);
            account.setPending(true);
            account.setUsername(createCredentialsRequest.getUsername());
            account.setPassword(this.encoder.encode(createCredentialsRequest.getPassword()));
            this.accountService.save(account);
            return this.fromAccount(account);
        }
    }

    @Override
    public User updateProfile(ProfileUpdateRequest updateRequest, User user) throws UsernameNotFoundException, FieldValidationException {
        this.authorizeActionOn(updateRequest.getId(), false);
        UserAccount account = this.accountService.getAccountById(updateRequest.getId());
        if(account == null) {
            throw new UsernameNotFoundException(updateRequest.getId());
        }

        // Password
        if(!Strings.isNullOrEmpty(updateRequest.getPassword())) {
            this.throwOnValidationFail(this.validatePassword(updateRequest.getPassword()));
            account.setPassword(this.encoder.encode(updateRequest.getPassword()));
        }

        // Name
        this.throwOnValidationFail(this.validateString("name", updateRequest.getFullName()));
        account.setFullName(updateRequest.getFullName());

        // Only admin can change email
        if(user != null && user.isAdministrator()) {
            // Email
            this.throwOnValidationFail(this.validateEmail(updateRequest.getEmail(), account.getId()));
            account.setEmail(updateRequest.getEmail());
            account.setVerified(true);
        }

        // Organization
        this.throwOnValidationFail(this.validateString("organization", updateRequest.getOrganization()));
        account.setOrganization(updateRequest.getOrganization());

        this.accountService.save(account);
        return this.fromAccount(account);
    }

    @Override
    public UserAccount internalUpdate(ProfileUpdateRequest updateRequest, UserAccount account) throws UsernameNotFoundException, FieldValidationException {
        if(account == null) {
            throw new UsernameNotFoundException(updateRequest.getId());
        }

        // Password
        if(!Strings.isNullOrEmpty(updateRequest.getPassword())) {
            this.throwOnValidationFail(this.validatePassword(updateRequest.getPassword()));
            account.setPassword(this.encoder.encode(updateRequest.getPassword()));
        }

        // Name
        this.throwOnValidationFail(this.validateString("name", updateRequest.getFullName()));
        account.setFullName(updateRequest.getFullName());

        // Only admin can change email
        this.throwOnValidationFail(this.validateEmail(updateRequest.getEmail(), account.getId()));
        account.setEmail(updateRequest.getEmail());
        account.setVerified(true);

        // Organization
        this.throwOnValidationFail(this.validateString("organization", updateRequest.getOrganization()));
        account.setOrganization(updateRequest.getOrganization());

        this.accountService.save(account);
        return account;
    }

    @Override
    public User changePassword(UserAccount account, String password) throws UsernameNotFoundException, FieldValidationException {
        this.throwOnValidationFail(this.validatePassword(password));
        account.setPassword(this.encoder.encode(password));
        this.accountService.save(account);
        return this.fromAccount(account);
    }

    @Override
    public User findUserByUsername(String username) {
        UserAccount account = this.accountService.getAccountByUsername(username);
        return account != null ? this.fromAccount(account) : null;
    }

    @Override
    public User findUserByIssuer(String issuer, String issuerId) {
        UserAccount account = this.findUserAccountByIssuer(issuer, issuerId);
        return account != null ? this.fromAccount(account) : null;
    }

    @Override
    public UserAccount findUserAccountByIssuer(String issuer, String issuerId) {
        return this.accountRepository.findByIssuerIdListIsContaining(new IssuerId(issuer, issuerId));
    }

    @Override
    public User findUserByIdOrFail(String userId) throws NotFoundException {
        UserAccount account = this.accountService.getAccountById(userId);
        if(account == null) {
            throw new NotFoundException(userId);
        } else {
            return this.fromAccount(account);
        }
    }

    @Override
    public User findUserById(String userId) {
        UserAccount account = this.accountService.getAccountById(userId);
        if(account == null) {
            return null;
        } else {
            return this.fromAccount(account);
        }
    }

    @Override
    public String getUserDisplayName(String userId) {
        UserAccount account = this.accountService.getAccountById(userId);
        return this.getUserDisplayName(account);
    }

    public String getUserDisplayName(UserAccount account) {
        if(account == null) {
            return "Unknown User";
        } else {
            return !Strings.isNullOrEmpty(account.getUsername()) ? account.getUsername() : account.getFullName();
        }
    }

    @Override
    public Set<UserRole> createPrivilegeFromRole(Set<String> roles) {
        return null;
    }

    @Override
    public boolean exists(String username) {
        return this.accountService.exists(username);
    }

    @Override
    public boolean existsOrFail(String username) {
        if(!this.exists(username)) {
            throw new UsernameNotFoundException(username);
        }
        return true;
    }

    @Override
    public boolean authorizeActionOn(String userId, boolean privileged) throws AccessDeniedException {
        User user = this.getAuthenticationPrincipalAs();
        User account = this.findUserById(userId);

        if(account == null) {
            throw new AccessDeniedException("Unauthorized action");
        }

        if(account.getUsername() != null && account.getUsername().equals("admin") && privileged) {
            throw new AccessDeniedException("Unauthorized action, admin user can't be modified");
        }

        if(user.isAdministrator()) {
            return true;
        }

        if(!privileged && user.getId().equals(userId)) {
            return true;
        }

        throw new AccessDeniedException("Unauthorized action, not enough privileges");
    }

    @Override
    public User getAuthenticationPrincipalAs() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        if(authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    void throwOnValidationFail(FieldValidation validation) throws FieldValidationException {
        if(!validation.isStatus()) {
            throw new FieldValidationException(validation);
        }
    }

    public FieldValidation validatePassword(String password) {
        String expectation = "Your password must be 8-20 characters long, might contain upper and lower case letters and numbers and a special character (@*#!'&).";
        return new FieldValidation(!isNullOrEmpty(password) && password.matches("[a-zA-Z0-9@*#!'&]{8,20}"), expectation);
    }

    public FieldValidation validateEmail(String email, String exclude) {
        String expectation = "Your email must be in a valid email format.";
        boolean format = !isNullOrEmpty(email) && email.matches("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$");
        if(!format) {
            return new FieldValidation(false, expectation);
        } else {
            UserAccount account = this.accountRepository.findByEmail(email);
            if(account != null && !account.getId().equals(exclude)) {
                return new FieldValidation(false, "email already used");
            }
        }
        return new FieldValidation(true, expectation);
    }

    public FieldValidation validateString(String field, String value) {
        String expectation = "Your " + field + " must not be empty.";
        return new FieldValidation(!isNullOrEmpty(value), expectation);
    }

    public FieldValidation validateUsername(String value) {
        String expectation = "Your username must be 5-10 characters long, might contain letters and numbers and underscore, and must not contain spaces, special characters";
        boolean format = !isNullOrEmpty(value) && value.matches("[a-zA-Z0-9_]{5,10}");
        if(!format) {
            return new FieldValidation(false, expectation);
        } else {
            if(this.accountService.exists(value)) {
                return new FieldValidation(false, "Username already used");
            }
        }
        return new FieldValidation(true, expectation);
    }
}
