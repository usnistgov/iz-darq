package gov.nist.healthcare.iz.darq.boot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.service.AccountService;

import gov.nist.healthcare.iz.darq.model.CVXCode;
import gov.nist.healthcare.iz.darq.model.FileDownload;

import gov.nist.healthcare.iz.darq.repository.CVXRepository;
import gov.nist.healthcare.iz.darq.service.impl.SimpleDownloadService;
import gov.nist.healthcare.iz.darq.service.utils.DownloadService;

@SpringBootApplication
@ComponentScan(basePackages = { "gov.nist.healthcare.iz.darq", "gov.nist.healthcare.auth" })
public class Application extends SpringBootServletInitializer{

	@Autowired
	private AccountService accountService;
	@Autowired
	private CVXRepository cvxRepo;

	
	@Value("#{environment.DARQ_STORE}")
	private String ADF_FOLDER;
	@Value("#{environment.DARQ_KEY}")
	private String KEY_FOLDER;
	@Value("${darq.admin.default}") 
	private String ADMIN_PASSWORD;

	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
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
		test.setPassword(ADMIN_PASSWORD);

		if (accountService.getAccountByUsername("admin") == null) {
			accountService.createAdmin(test);
		}
		
		this.createCVX();
		this.setup();
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
	
	@Bean
	public DownloadService downloadService() throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<HashMap<String,FileDownload>> typeRef = new TypeReference<HashMap<String,FileDownload>>() {};
		Map<String, FileDownload> map = mapper.readValue(Application.class.getResourceAsStream("/docs.json"), typeRef);
		return new SimpleDownloadService(map);
	}
	
	
	public void createCliJAR() {
		
	}
	
	public void checkStoreIntegrity() {
		
	}
	
    
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