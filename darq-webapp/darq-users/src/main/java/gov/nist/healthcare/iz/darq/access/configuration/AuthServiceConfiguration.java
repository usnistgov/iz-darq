package gov.nist.healthcare.iz.darq.auth.service.configuration;

import gov.nist.healthcare.auth.config.JWTAuthenticationFilter;
import gov.nist.healthcare.auth.config.JWTLoginFilter;
import gov.nist.healthcare.auth.config.JWTTokenAuthenticationService;
import gov.nist.healthcare.auth.service.AccountService;
import gov.nist.healthcare.auth.service.CryptoKey;
import gov.nist.healthcare.auth.service.impl.DefaultAccountService;
import gov.nist.healthcare.iz.darq.access.domain.UserRole;
import gov.nist.healthcare.iz.darq.users.domain.UserAccount;
import gov.nist.healthcare.iz.darq.users.repository.UserAccountRepository;
import gov.nist.healthcare.iz.darq.users.repository.UserRoleRepository;
import gov.nist.healthcare.iz.darq.users.service.impl.AuthenticationService;
import gov.nist.healthcare.iz.darq.users.service.impl.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
public class AuthServiceConfiguration {

    @Value( "${gov.nist.healthcare.iz.auth.token.expiration.seconds}" )
    int DURATION;
    @Value( "${gov.nist.healthcare.iz.auth.cookie.name}" )
    String COOKIE_NAME;
    @Value( "${gov.nist.healthcare.iz.auth.filter.url}" )
    String FILTER_URL;
    @Value("${darq.auth.privilege.super}")
    private String ADMIN_PRIVILEGE;
    @Value("${darq.auth.privilege.default}")
    private String USER_PRIVILEGE;
    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    UserRoleRepository userRoleRepository;
    @Autowired
    CryptoKey cryptoKey;
    @Autowired
    AuthenticationEntryPoint handler;
    @Autowired
    AuthenticationManager authManager;

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JWTAuthenticationFilter<UserAccount, UserRole> authenticationFilter(JWTTokenAuthenticationService<UserAccount, UserRole> tokenAuthenticationService) {
        return new JWTAuthenticationFilter<>(tokenAuthenticationService);
    }

    @Bean
    public JWTTokenAuthenticationService<UserAccount, UserRole> tokenAuthenticationService(AuthenticationService authenticationService) {
        return new JWTTokenAuthenticationService<>(this.DURATION, this.COOKIE_NAME, this.cryptoKey, authenticationService);
    }
    @Bean
    public JWTLoginFilter<UserAccount, UserRole> loginFilter(JWTTokenAuthenticationService<UserAccount, UserRole> tokenAuthenticationService) {
        return new JWTLoginFilter<>(FILTER_URL, UserAccount.class, authManager, handler, tokenAuthenticationService);
    }

    @Bean
    public AuthenticationService authenticationService(UserManagementService userManagementService) {
        return new AuthenticationService(userManagementService);
    }

    @Bean
    public AccountService<UserAccount, UserRole> accountService(PasswordEncoder passwordEncoder) {
        return new DefaultAccountService<>(userAccountRepository, userRoleRepository, passwordEncoder);
    }

    @Bean
    public UserManagementService userManagementService(AccountService<UserAccount, UserRole> accountService) {
        return new UserManagementService(accountService, ADMIN_PRIVILEGE, USER_PRIVILEGE);
    }
}
