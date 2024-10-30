package gov.nist.healthcare.iz.darq.boot.config.data;

import com.mongodb.MongoCredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import java.util.Collections;

@Configuration
@EnableMongoRepositories(basePackages={"gov.nist.healthcare.iz.darq" , "gov.nist.healthcare.auth"})
public class MongoConfig extends AbstractMongoConfiguration {

	@Value("${darq.db.host}")
	private String HOST;
	@Value("${darq.db.port}")
	private String PORT;
	@Value("${darq.db.name}")
	private String NAME;
	@Value("${darq.db.username}")
	private String USERNAME;
	@Value("${darq.db.password}")
	private String PASSWORD;
	@Value("${darq.db.auth.source}")
	private String AUTH_SOURCE;

	@Autowired
	private MappingMongoConverter mongoConverter;

	@Bean
	public GridFsTemplate gridFsTemplate() throws Exception {
		return new GridFsTemplate(mongoDbFactory(), mongoConverter);
	}
	
	@Override
	protected String getDatabaseName() {
		return NAME;
	}

	@Override
	public Mongo mongo() {
		if(USERNAME != null && PASSWORD != null && !USERNAME.isEmpty() && !PASSWORD.isEmpty()) {
			MongoCredential credential = MongoCredential.createCredential(USERNAME, AUTH_SOURCE, PASSWORD.toCharArray());
			return new MongoClient(
					new ServerAddress(
							HOST,
							Integer.parseInt(PORT)
					),
					Collections.singletonList(credential)
			);
		}
		return new MongoClient(new ServerAddress(HOST, Integer.parseInt(PORT)));
	}

}