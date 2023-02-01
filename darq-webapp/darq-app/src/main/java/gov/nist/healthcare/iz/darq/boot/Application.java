package gov.nist.healthcare.iz.darq.boot;

import java.io.*;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;

import com.google.common.base.Strings;
import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.crypto.service.impl.JKSCryptoKey;
import gov.nist.healthcare.iz.darq.adf.utils.crypto.CryptoUtils;
import gov.nist.healthcare.iz.darq.model.*;
import gov.nist.healthcare.iz.darq.repository.EmailTemplateRepository;
import gov.nist.healthcare.iz.darq.service.impl.SimpleEmailService;
import gov.nist.healthcare.iz.darq.service.impl.WebContentService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.immregistries.codebase.client.CodeMap;
import org.immregistries.codebase.client.CodeMapBuilder;
import org.immregistries.codebase.client.generated.Code;
import org.immregistries.codebase.client.reference.CodesetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nist.healthcare.iz.darq.repository.CVXRepository;
import gov.nist.healthcare.iz.darq.service.impl.SimpleDownloadService;
import gov.nist.healthcare.iz.darq.service.utils.DownloadService;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, FreeMarkerAutoConfiguration.class})
@EnableWebMvc
@ComponentScan(basePackages = { "gov.nist.healthcare.iz.darq", "gov.nist.healthcare.auth" })
public class Application extends SpringBootServletInitializer{

	@Autowired
	private Environment env;
	@Autowired
	EmailTemplateRepository emailTemplateRepository;
	@Autowired
	WebContentService webContentService;
	@Autowired
	CryptoUtils cryptoUtils;

	@Autowired
	private CVXRepository cvxRepo;
	@Value("#{environment.QDAR_STORE}")
	private String QDAR_STORE;

	@Value("#{environment.QDAR_AUTH_JKS_PATH}")
	private String QDAR_AUTH_JKS_PATH;
	@Value("#{environment.QDAR_AUTH_JKS_PASSWORD}")
	private String QDAR_AUTH_JKS_PASSWORD;
	@Value("#{environment.QDAR_AUTH_KEY_ALIAS}")
	private String QDAR_AUTH_KEY_ALIAS;
	@Value("#{environment.QDAR_AUTH_KEY_PASSWORD}")
	private String QDAR_AUTH_KEY_PASSWORD;

	@Value("#{environment.QDAR_ADF_JKS_PATH}")
	private String QDAR_ADF_JKS_PATH;
	@Value("#{environment.QDAR_ADF_JKS_PASSWORD}")
	private String QDAR_ADF_JKS_PASSWORD;
	@Value("#{environment.QDAR_ADF_KEY_ALIAS}")
	private String QDAR_ADF_KEY_ALIAS;
	@Value("#{environment.QDAR_ADF_KEY_PASSWORD}")
	private String QDAR_ADF_KEY_PASSWORD;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	//---------------------------------------------------
	//---------------------- BEANS ----------------------
	//---------------------------------------------------

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

	@Bean
	public InternalResourceViewResolver defaultViewResolver() {
		return new InternalResourceViewResolver();
	}

	@Bean
	public Jackson2ObjectMapperBuilder jacksonBuilder() {
		return new Jackson2ObjectMapperBuilder();
	}

	@Bean
	public MultipartResolver multipartResolver() {
		return new CommonsMultipartResolver();
	}

	@Bean
	public DownloadService downloadService(@Qualifier("ADF_KEYS") CryptoKey cryptoKey) {
		return new SimpleDownloadService(cryptoKey);
	}


	@Bean
	public SimpleEmailService emailService() throws MessagingException, IOException {
		SimpleEmailService emailService = new SimpleEmailService(this.emailTemplateRepository, this.env);
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<List<EmailTemplate>> typeRef = new TypeReference<List<EmailTemplate>>() {};
		List<EmailTemplate> templates = mapper.readValue(Application.class.getResourceAsStream("/email-templates.json"), typeRef);
		for(EmailTemplate template: templates) {
			emailService.setEmailTemplateIfAbsent(template);
		}
		return emailService;
	}

	@Bean("AUTH_KEYS")
	public CryptoKey authCryptoKeys() throws Exception {
		List<String> errors = new ArrayList<>();
		if(Strings.isNullOrEmpty(QDAR_AUTH_JKS_PATH)) {
			errors.add("Could not find environment Variable QDAR_AUTH_JKS_PATH");
		}
		if(Strings.isNullOrEmpty(QDAR_AUTH_JKS_PASSWORD)) {
			errors.add("Could not find environment Variable QDAR_AUTH_JKS_PASSWORD");
		}
		if(Strings.isNullOrEmpty(QDAR_AUTH_KEY_ALIAS)) {
			errors.add("Could not find environment Variable QDAR_AUTH_KEY_ALIAS");
		}
		if(Strings.isNullOrEmpty(QDAR_AUTH_KEY_PASSWORD)) {
			errors.add("Could not find environment Variable QDAR_AUTH_KEY_PASSWORD");
		}

		if(errors.size() > 0) {
			throw new Exception("[QDAR_AUTH_ENV] One or more environment variables are missing : " + String.join(",", errors));
		} else {
			try {
				return new JKSCryptoKey(QDAR_AUTH_JKS_PATH, QDAR_AUTH_KEY_ALIAS, QDAR_AUTH_JKS_PASSWORD, QDAR_AUTH_KEY_PASSWORD);
			} catch (Exception exception) {
				throw new Exception("[QDAR_AUTH_KEYS] Failed to read AUTH Keys", exception);
			}
		}
	}

	@Bean("ADF_KEYS")
	public CryptoKey adfCryptoKeys() throws Exception {
		List<String> errors = new ArrayList<>();
		if(Strings.isNullOrEmpty(QDAR_ADF_JKS_PATH)) {
			errors.add("Could not find environment Variable QDAR_ADF_JKS_PATH");
		}
		if(Strings.isNullOrEmpty(QDAR_ADF_JKS_PASSWORD)) {
			errors.add("Could not find environment Variable QDAR_ADF_JKS_PASSWORD");
		}
		if(Strings.isNullOrEmpty(QDAR_ADF_KEY_ALIAS)) {
			errors.add("Could not find environment Variable QDAR_ADF_KEY_ALIAS");
		}
		if(Strings.isNullOrEmpty(QDAR_ADF_KEY_PASSWORD)) {
			errors.add("Could not find environment Variable QDAR_ADF_KEY_PASSWORD");
		}

		if(errors.size() > 0) {
			throw new Exception("[QDAR_ADF_ENV] One or more environment variables are missing : " + String.join(",", errors));
		} else {
			try {
				return new JKSCryptoKey(QDAR_ADF_JKS_PATH, QDAR_ADF_KEY_ALIAS, QDAR_ADF_JKS_PASSWORD, QDAR_ADF_KEY_PASSWORD);
			} catch (Exception exception) {
				throw new Exception("[QDAR_ADF_KEYS] Failed to read ADF Keys", exception);
			}
		}
	}

	//-----------------------------------------------------
	//---------------------- STARTUP ----------------------
	//-----------------------------------------------------

	@PostConstruct()
	public void setWebContent() throws IOException {
		boolean exist = this.webContentService.exists();
		if(!exist) {
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<WebContent> typeRef = new TypeReference<WebContent>() {};
			WebContent webcontent = mapper.readValue(Application.class.getResourceAsStream("/web-content.json"), typeRef);
			this.webContentService.save(webcontent);
		}
	}

	@PostConstruct()
	public void createCVX() {
		CodeMap codeMap = CodeMapBuilder.INSTANCE.getCompiledCodeMap();
		Collection<Code> codes = codeMap.getCodesForTable(CodesetType.VACCINATION_CVX_CODE);

		for(Code code: codes) {
			CVXCode cvx = new CVXCode();
			cvx.setCvx(code.getValue());
			cvx.setName(code.getLabel());
			this.cvxRepo.save(cvx);
		}
	}
	
    @PostConstruct
    public void setup() throws Exception {
    	if(Strings.isNullOrEmpty(QDAR_STORE)){
			throw new Exception("[QDAR_ADF_STORE] Could not find environment Variable QDAR_STORE");
    	}
    }
}