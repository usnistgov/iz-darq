package gov.nist.healthcare.iz.darq.config.data;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

@Configuration
@EnableMongoRepositories(basePackages={"gov.nist.healthcare.iz.darq" , "gov.nist.healthcare.auth"})
public class MongoConfig extends AbstractMongoConfiguration {

	@Override
	protected String getDatabaseName() {
		return "darq-db";
	}

	@Override
	public Mongo mongo() throws Exception {
		return new MongoClient(new ServerAddress("127.0.0.1",27017));
	}

	@Override
	protected String getMappingBasePackage() {
		return "gov.nist.healthcare.iz.darq";
	}

}