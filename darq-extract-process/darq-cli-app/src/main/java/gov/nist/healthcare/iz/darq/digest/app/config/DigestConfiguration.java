package gov.nist.healthcare.iz.darq.digest.app.config;

import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gov.nist.healthcare.iz.darq.adf.service.ADFStore;
import gov.nist.healthcare.iz.darq.digest.domain.ADFMetaData;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;

@Configuration
public class DigestConfiguration {

	@Bean
	public ADFStore store(){
		return new ADFStore() {

			@Override
			public String store(ADFMetaData metadata) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ADFMetaData get(String id, String owner) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ADFMetaData getAccessible(String id, String owner) throws Exception {
				return null;
			}

			@Override
			public ADFile getFile(String id, String owner) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean delete(String id, String owner) throws IOException {
				// TODO Auto-generated method stub
				return false;
			}
			
		};
	}
	
	
}
