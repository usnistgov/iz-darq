package gov.nist.healthcare.iz.darq.boot;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.service.AccountService;
import gov.nist.healthcare.iz.darq.batch.domain.JobData;
import gov.nist.healthcare.iz.darq.batch.service.JobManager;

@SpringBootApplication
@ComponentScan(basePackages = { "gov.nist.healthcare.iz.darq", "gov.nist.healthcare.auth" })
public class Application {

	@Autowired
	private AccountService accountService;

	@Autowired
	private JobManager jobManager;
	
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
	
	
	public void testJobs() throws IOException, Exception{
		JobData jobData = new JobData();
		jobData.setName("test-job");
		jobData.setUser("admin");
		jobData.setConfiguration(null);
		FileInputStream patient = new FileInputStream(Paths.get("/Users/hnt5/generated_data/patients.data").toFile());
		FileInputStream vaccines = new FileInputStream(Paths.get("/Users/hnt5/generated_data/vaccines.data").toFile());
		this.jobManager.createJob(jobData, patient, vaccines, "admin");
		
		JobData jobData2 = new JobData();
		jobData2.setName("test-job");
		jobData2.setUser("admin");
		jobData2.setConfiguration(null);
		FileInputStream patients = new FileInputStream(Paths.get("/Users/hnt5/generated_data/patients.data").toFile());
		FileInputStream vacciness = new FileInputStream(Paths.get("/Users/hnt5/generated_data/vaccines.data").toFile());
		this.jobManager.createJob(jobData2, patients, vacciness, "admin");
		
		while(true){
			Thread.sleep(5000);
			List<JobData> jobs = this.jobManager.getJobs("admin");
			System.out.println(jobs);
		}
	}
}