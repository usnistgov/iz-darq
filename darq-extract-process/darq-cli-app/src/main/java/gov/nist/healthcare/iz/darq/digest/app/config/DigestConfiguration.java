package gov.nist.healthcare.iz.darq.digest.app.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.writer.sqlite.SqliteADFWriter;
import gov.nist.healthcare.iz.darq.detections.AvailableDetectionEngines;
import gov.nist.healthcare.iz.darq.detections.DetectionEngine;
import gov.nist.healthcare.iz.darq.detections.DetectionProvider;
import gov.nist.healthcare.iz.darq.digest.service.detection.provider.mismo.MismoMatcherDetectionProvider;
import gov.nist.healthcare.iz.darq.digest.service.detection.provider.mqe.MQEDetectionProvider;
import gov.nist.healthcare.iz.darq.digest.service.impl.PublicOnlyCryptoKey;
import gov.nist.healthcare.iz.darq.digest.service.patient.matching.mismo.MismoPatientMatchingService;
import gov.nist.healthcare.iz.darq.patient.matching.service.mismo.MismoPatientMatcherService;
import gov.nist.healthcare.iz.darq.patient.matching.service.mismo.MismoSQLitePatientBlockHandler;
import org.immregistries.mismo.match.PatientMatcher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gov.nist.healthcare.iz.darq.adf.service.ADFStore;
import gov.nist.healthcare.iz.darq.digest.domain.ADFMetaData;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;

@Configuration
public class DigestConfiguration {

	@Bean
	@Qualifier("ADF_KEYS")
	public CryptoKey getAdfCryptoKey() throws Exception {
		return new PublicOnlyCryptoKey();
	}

	@Bean
	@Qualifier("MATCHER_SERVICE")
	public MismoPatientMatchingService patientMatchingService() {
	  MismoPatientMatcherService matcher = new MismoPatientMatcherService(new PatientMatcher());
	  MismoSQLitePatientBlockHandler blockHandler = new MismoSQLitePatientBlockHandler();
	  return new MismoPatientMatchingService(matcher, blockHandler);
	}

	@Bean
	public DetectionEngine detectionEngine() {
		MQEDetectionProvider mqeDetectionProvider = new MQEDetectionProvider();
		MismoMatcherDetectionProvider mismoMatcherDetectionProvider = new MismoMatcherDetectionProvider(patientMatchingService());
		Map<String, DetectionProvider> providers = new HashMap<>();
		providers.put(AvailableDetectionEngines.DP_ID_MQE, mqeDetectionProvider);
		providers.put(AvailableDetectionEngines.DP_ID_PM, mismoMatcherDetectionProvider);
		return new DetectionEngine(providers);
	}

	@Bean
	public SqliteADFWriter adfWriter() {
		return new SqliteADFWriter();
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
			public ADFile getFile(String id) throws Exception {
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
