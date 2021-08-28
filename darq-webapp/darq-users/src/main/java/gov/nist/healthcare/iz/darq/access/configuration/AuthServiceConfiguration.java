package gov.nist.healthcare.iz.darq.access.configuration;

import gov.nist.healthcare.auth.config.JWTAuthenticationFilter;
import gov.nist.healthcare.auth.config.JWTTokenAuthenticationService;
import gov.nist.healthcare.auth.service.AccountService;
import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.auth.service.impl.DefaultAccountService;
import gov.nist.healthcare.iz.darq.access.domain.UserRole;
import gov.nist.healthcare.iz.darq.repository.FacilityRepository;
import gov.nist.healthcare.iz.darq.service.impl.SimpleEmailService;
import gov.nist.healthcare.iz.darq.users.domain.User;
import gov.nist.healthcare.iz.darq.users.domain.UserAccount;
import gov.nist.healthcare.iz.darq.users.facility.service.FacilityService;
import gov.nist.healthcare.iz.darq.users.facility.service.FacilityServiceImpl;
import gov.nist.healthcare.iz.darq.users.repository.UserAccountRepository;
import gov.nist.healthcare.iz.darq.users.repository.UserEditTokenRepository;
import gov.nist.healthcare.iz.darq.users.repository.UserRoleRepository;
import gov.nist.healthcare.iz.darq.users.service.UserTokenizedEditService;
import gov.nist.healthcare.iz.darq.users.service.impl.AuthenticationService;
import gov.nist.healthcare.iz.darq.users.service.impl.SimpleUserTokenizedEditService;
import gov.nist.healthcare.iz.darq.users.service.impl.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
public class AuthServiceConfiguration {

    @Value( "${gov.nist.healthcare.iz.auth.cookie.name}" )
    String COOKIE_NAME;
    @Value( "${gov.nist.healthcare.iz.auth.token.expiration.seconds}" )
    int DURATION;
    @Value("${darq.auth.privilege.super}")
    private String ADMIN_PRIVILEGE;
    @Value("${darq.auth.privilege.default}")
    private String USER_PRIVILEGE;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private UserEditTokenRepository userEditTokenRepository;
    @Autowired
    @Qualifier("AUTH_KEYS")
    private CryptoKey authCryptoKeys;
    @Autowired
    private FacilityRepository facilityRepository;
    @Autowired
    SimpleEmailService simpleEmailService;
    @Autowired
    Environment environment;
    @Autowired
    AuthenticationEntryPoint handler;

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JWTAuthenticationFilter<UserAccount, UserRole, User> authenticationFilter(JWTTokenAuthenticationService<UserAccount, UserRole, User> tokenAuthenticationService) {
        return new JWTAuthenticationFilter<>(tokenAuthenticationService, handler);
    }

    @Bean
    public JWTTokenAuthenticationService<UserAccount, UserRole, User> tokenAuthenticationService(AuthenticationService authenticationService) {
        return new JWTTokenAuthenticationService<>(this.COOKIE_NAME, this.authCryptoKeys, authenticationService);
    }

    @Bean
    public AuthenticationService authenticationService(UserManagementService userManagementService, FacilityService facilityService) {
        return new AuthenticationService(userManagementService, handler, facilityService, this.authCryptoKeys, COOKIE_NAME, DURATION);
    }

    @Bean
    public AccountService<UserAccount, UserRole> accountService(PasswordEncoder passwordEncoder) {
        return new DefaultAccountService<>(userAccountRepository, userRoleRepository, passwordEncoder);
    }

    @Bean
    public UserManagementService userManagementService(AccountService<UserAccount, UserRole> accountService, PasswordEncoder encoder) {
        return new UserManagementService(accountService, userAccountRepository, encoder, ADMIN_PRIVILEGE, USER_PRIVILEGE);
    }

    @Bean
    public FacilityService facilityService(UserManagementService userManagementService) {
        return new FacilityServiceImpl(facilityRepository, userManagementService);
    }

    @Bean
    public UserTokenizedEditService userTokenizedEditService(UserManagementService userManagementService) {
        return new SimpleUserTokenizedEditService(simpleEmailService, userEditTokenRepository, userAccountRepository, userManagementService, environment);
    }

}
