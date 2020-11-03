package gov.nist.healthcare.iz.darq.boot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;

import gov.nist.healthcare.iz.darq.model.*;
import gov.nist.healthcare.iz.darq.repository.EmailTemplateRepository;
import gov.nist.healthcare.iz.darq.service.impl.SimpleEmailService;
import gov.nist.healthcare.iz.darq.service.impl.WebContentService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
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
	private CVXRepository cvxRepo;
	@Value("#{environment.DARQ_STORE}")
	private String ADF_FOLDER;
	@Value("#{environment.DARQ_KEY}")
	private String KEY_FOLDER;


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
	public DownloadService downloadService() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<HashMap<String,FileDownload>> typeRef = new TypeReference<HashMap<String,FileDownload>>() {};
		Map<String, FileDownload> map = mapper.readValue(Application.class.getResourceAsStream("/docs.json"), typeRef);
		return new SimpleDownloadService(map);
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
	public void createCVX() throws IOException{
		if(this.cvxRepo.count() > 0) return;
		
		InputStream file = Application.class.getResourceAsStream("/cvx.xlsx");
		Workbook workbook = new XSSFWorkbook(file);
		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> it = sheet.rowIterator();
		it.next();
		while(it.hasNext()){
			Row row = it.next();
			CVXCode code = new CVXCode();
			code.setCvx(row.getCell(0).getStringCellValue());
			code.setName(row.getCell(1).getStringCellValue());
			this.cvxRepo.save(code);
		}
		workbook.close();
	}
	
    @PostConstruct
    public void setup() throws Exception {
    	
    	//-- Verify ENV
    	boolean b = true;
    	if(ADF_FOLDER == null || ADF_FOLDER.isEmpty()){
    		b = false;
    		System.out.println("[DARQ-SETUP] Could not find environment Variable DARQ_STORE");
    	}
    		
    	if(KEY_FOLDER == null || KEY_FOLDER.isEmpty()){
    		b = false;
    		System.out.println("[DARQ-SETUP] Could not find environment Variable DARQ_KEY");
    	}
    	
    	if(!b) throw new Exception("One or more Environement Variables were not found");
    			
    	//-- Verify ADF Folder
    	File f = new File(ADF_FOLDER);
    	if(!f.exists()){
    		f.mkdirs();
    		if(!f.exists()){
    			throw new Exception("Could not create directory : "+ADF_FOLDER);
    		}
    	}
    	
    	//-- Verify Keys
    	File k = new File(KEY_FOLDER);
    	if(!k.exists()){
    		throw new Exception("Could not find directory : "+KEY_FOLDER);
    	}
    	else {
    		File pub = new File(KEY_FOLDER+"/certificate.pub");
    		File priv = new File(KEY_FOLDER+"/certificate.key");
    		if(!pub.exists()){
    			throw new Exception("Could not find PUBLIC KEY : "+KEY_FOLDER+"/certificate.pub");
    		}
    		if(!priv.exists()){
    			throw new Exception("Could not find PRIVATE KEY : "+KEY_FOLDER+"/certificate.priv");
    		}
    	}
    }
}