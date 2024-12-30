package gov.nist.healthcare.iz.darq.digest.app.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.module.ADFManager;
import gov.nist.healthcare.iz.darq.adf.module.json.BsonADFModule;
import gov.nist.healthcare.iz.darq.detections.AvailableDetectionEngines;
import gov.nist.healthcare.iz.darq.detections.DetectionEngine;
import gov.nist.healthcare.iz.darq.detections.DetectionProvider;
import gov.nist.healthcare.iz.darq.digest.service.detection.provider.mismo.MismoMatcherDetectionProvider;
import gov.nist.healthcare.iz.darq.digest.service.detection.provider.mqe.MQEDetectionProvider;
import gov.nist.healthcare.iz.darq.digest.service.impl.PublicOnlyCryptoKey;
import gov.nist.healthcare.iz.darq.digest.service.patient.matching.mismo.MismoPatientMatchingService;
import gov.nist.healthcare.iz.darq.digest.service.report.ReportEngine;
import gov.nist.healthcare.iz.darq.digest.service.report.instances.BadPhoneNumberReportService;
import gov.nist.healthcare.iz.darq.digest.service.report.instances.DuplicateRecordsReportService;
import gov.nist.healthcare.iz.darq.digest.service.report.instances.LotNumberReportService;
import gov.nist.healthcare.iz.darq.digest.service.report.instances.PlaceholderNameReportService;
import gov.nist.healthcare.iz.darq.patient.matching.service.mismo.MismoPatientMatcherService;
import gov.nist.healthcare.iz.darq.patient.matching.service.mismo.MismoSQLitePatientBlockHandler;
import org.immregistries.mismo.match.PatientMatcher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
	  MismoPatientMatcherService matcher = new MismoPatientMatcherService(new PatientMatcher(
			  DigestConfiguration.class.getResourceAsStream("/Configuration.yml")
	  ));
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
	public ReportEngine reportEngine() {
		return new ReportEngine(
				Arrays.asList(
						new BadPhoneNumberReportService(),
						new DuplicateRecordsReportService(),
						new LotNumberReportService(),
						new PlaceholderNameReportService()
				)
		);
	}

	@Bean
	public ADFManager adfWriter() throws Exception {
		ADFManager manager = new ADFManager();
		manager.register(new BsonADFModule(), true, false);
		return manager;
	}
	
}
