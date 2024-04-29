package gov.nist.healthcare.iz.darq.access.configuration;


import gov.nist.healthcare.iz.darq.access.domain.UserRole;
import gov.nist.healthcare.iz.darq.auth.aart.JWTAuthenticationAARTClientFilter;
import gov.nist.healthcare.iz.darq.auth.aart.JWTAuthenticationAARTFilter;
import gov.nist.healthcare.iz.darq.users.domain.User;
import gov.nist.healthcare.iz.darq.users.domain.UserAccount;
import gov.nist.healthcare.iz.darq.users.service.impl.AuthenticationService;
import gov.nist.healthcare.iz.darq.users.service.impl.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import gov.nist.healthcare.auth.config.JWTAuthenticationFilter;
import gov.nist.healthcare.auth.config.JWTLoginFilter;
import gov.nist.healthcare.auth.service.AccountService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private AccountService<UserAccount, UserRole> accountService;
	@Autowired
	private JWTAuthenticationFilter<UserAccount, UserRole, User> authFilter;
	@Autowired
	private AuthenticationManager authManager;
	@Value("${darq.auth.privilege.super}")
	private String ADMIN_PRIVILEGE;
	@Value( "${gov.nist.healthcare.iz.auth.filter.url}" )
	String FILTER_URL;
	@Autowired
	AuthenticationEntryPoint handler;
	@Autowired
	AuthenticationService authenticationService;
	@Autowired
	UserManagementService userManagementService;
	@Autowired
	Environment environment;

	@Bean
	public JWTLoginFilter<UserAccount, UserRole, User> loginFilter() {
		return new JWTLoginFilter<>(FILTER_URL, UserAccount.class, authManager, handler, authenticationService);
	}

	@Bean
	public JWTAuthenticationAARTFilter jwtAuthenticationAARTFilter() {
		return new JWTAuthenticationAARTFilter("/**/api/aart", userManagementService, authManager, authenticationService, handler, environment);
	}

	@Bean
	public JWTAuthenticationAARTClientFilter clientFilter() {
		return new JWTAuthenticationAARTClientFilter(environment, "/**/aart/client/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
		.authorizeRequests()
			.antMatchers("/**/public/**").permitAll()
			.antMatchers("/**/api/aart/**").permitAll()
			.antMatchers("/**/api/login").not().authenticated()
			.antMatchers("/**/api/user/register").not().authenticated()
			.antMatchers("/**/api/user/reset-password").not().authenticated()
			.antMatchers("/**/api/user/reset-password-request").not().authenticated()
			.antMatchers("/**/api/user/verify-email").not().authenticated()
			.antMatchers("/**/api/user/validate-token").not().authenticated()
			.antMatchers("/**/api/facility/**").hasAuthority(ADMIN_PRIVILEGE)
			.antMatchers("/**/api/account/**").hasAuthority(ADMIN_PRIVILEGE)
			.antMatchers("/**/api/**").fullyAuthenticated()
		.and()
			.exceptionHandling().authenticationEntryPoint(handler)
		.and()
			.addFilterBefore(loginFilter(), UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(jwtAuthenticationAARTFilter(), UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(clientFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(accountService).passwordEncoder(encoder);
	}
}