package gov.nist.healthcare.iz.darq.config.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import gov.nist.healthcare.auth.config.TokenAuthenticationService;
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
	private TokenAuthenticationService tokenService;
	
    @Bean
    protected JWTLoginFilter loginFilter(){
    	return new JWTLoginFilter("/**/api/login", authManager, tokenService);
    }
    
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
		
		.authorizeRequests()
			.antMatchers("/**/public/**").permitAll()
			.antMatchers("/**/api/login").permitAll()
			.antMatchers("/**/api/facility/**").hasAuthority("ADMIN")
			.antMatchers("/**/api/**").fullyAuthenticated()
		.and()
		.addFilterBefore(loginFilter(), UsernamePasswordAuthenticationFilter.class)
		.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(authenticationService).passwordEncoder(encoder);
	}
}