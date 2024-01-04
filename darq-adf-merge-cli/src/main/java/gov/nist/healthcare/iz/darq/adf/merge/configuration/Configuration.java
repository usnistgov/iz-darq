package gov.nist.healthcare.iz.darq.adf.merge.configuration;

import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.merge.model.LoadableCryptoKey;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.adf.service.ADFStore;
import gov.nist.healthcare.iz.darq.digest.domain.ADFMetaData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.io.InputStream;

@org.springframework.context.annotation.Configuration
public class Configuration {

	@Bean
	@Qualifier("ADF_KEYS")
	public CryptoKey getAdfCryptoKey() throws Exception {
		return new LoadableCryptoKey();
	}

	@Bean
	public ADFStore store(){
		return new ADFStore() {

			@Override
			public String store(ADFMetaData metadata) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ADFMetaData get(String id) {
				// TODO Auto-generated method stub
				return null;
			}


			@Override
			public ADFReader getFile(String id) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public InputStream getFileInputStream(String id) throws Exception {
				return null;
			}

			@Override
			public boolean delete(String id) throws IOException {
				// TODO Auto-generated method stub
				return false;
			}
			
		};
	}
	
	
}