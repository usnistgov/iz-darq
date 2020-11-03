package gov.nist.healthcare.iz.darq.boot.config.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import gov.nist.healthcare.auth.config.JWTAuthenticationFilter;
import gov.nist.healthcare.auth.config.JWTLoginFilter;
import gov.nist.healthcare.auth.config.JWTTokenAuthenticationService;
import gov.nist.healthcare.auth.service.AccountService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private AccountService authenticationService;
	@Autowired
	private JWTAuthenticationFilter authFilter;
	@Autowired
	private AuthenticationManager authManager;
	@Autowired
	private JWTTokenAuthenticationService tokenService;
	@Autowired
	private JWTLoginFilter loginFilter;
	@Value("${auth.privilege.super}")
	private String ADMIN_PRIVILEGE;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
		.authorizeRequests()
			.antMatchers("/**/public/**").permitAll()
			.antMatchers("/**/api/login").permitAll()
			.antMatchers("/**/api/user/register").not().authenticated()
			.antMatchers("/**/api/facility/**").hasAuthority(ADMIN_PRIVILEGE)
			.antMatchers("/**/api/account/**").hasAuthority(ADMIN_PRIVILEGE)
			.antMatchers("/**/api/**").fullyAuthenticated()
		.and()
		.addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class)
		.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(authenticationService).passwordEncoder(encoder);
	}
}