package gov.nist.healthcare.iz.darq.boot.config.data;

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

@Configuration
@EnableMongoRepositories(basePackages={"gov.nist.healthcare.iz.darq" , "gov.nist.healthcare.auth"})
public class MongoConfig extends AbstractMongoConfiguration {

	@Value("${darq.db.host}")
	private String HOST;
	@Value("${darq.db.port}")
	private String PORT;
	@Value("${darq.db.name}")
	private String NAME;

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
		return new MongoClient(new ServerAddress(HOST, Integer.parseInt(PORT)));
	}

}