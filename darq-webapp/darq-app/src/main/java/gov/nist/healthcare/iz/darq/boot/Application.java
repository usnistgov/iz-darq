package gov.nist.healthcare.iz.darq.boot;

import java.io.IOException;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.service.AccountService;

@SpringBootApplication
@ComponentScan(basePackages = { "gov.nist.healthcare.iz.darq", "gov.nist.healthcare.auth" })
public class Application {

	@Autowired
	private AccountService accountService;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public Jackson2ObjectMapperBuilder jacksonBuilder() {
		Jackson2ObjectMapperBuilder b = new Jackson2ObjectMapperBuilder();
		return b;
	}
	
	
	@Bean
	public BCryptPasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	@PostConstruct
	public void init() throws IOException, Exception {
		Account test = new Account();
		test.setUsername("admin");
		test.setPassword("qwerty");

		if (accountService.getAccountByUsername("admin") == null) {
			accountService.createAdmin(test);
		}
		
	}
	
	
	
	@Bean
	public MultipartResolver multipartResolver() {
	    CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
	    return multipartResolver;
	}
	
	@Value("${auth.roles}")
	public void createPrivileges(String roles){
		if(roles != null && !roles.isEmpty()){
			String[] privs = roles.split(",");
			for(String priv : privs){
				this.accountService.createPrivilegeByRole(priv);
			}
		}
	}
	
}