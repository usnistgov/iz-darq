package gov.nist.healthcare.iz.darq.digest.app.config;

import java.util.HashMap;
import java.util.Map;

import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.module.ADFManager;
import gov.nist.healthcare.iz.darq.adf.module.json.BsonADFModule;
import gov.nist.healthcare.iz.darq.detections.AvailableDetectionEngines;
import gov.nist.healthcare.iz.darq.detections.DetectionEngine;
import gov.nist.healthcare.iz.darq.detections.DetectionProvider;
import gov.nist.healthcare.iz.darq.detections.codes.VaccinationDuplicateDetection;
import gov.nist.healthcare.iz.darq.digest.service.detection.provider.complex.ComplexDetectionProvider;
import gov.nist.healthcare.iz.darq.digest.service.detection.provider.mismo.MismoMatcherDetectionProvider;
import gov.nist.healthcare.iz.darq.digest.service.detection.provider.mqe.MQEDetectionProvider;
import gov.nist.healthcare.iz.darq.digest.service.detection.provider.vaccinationduplicate.VaccinationDuplicateDetectionProvider;
import gov.nist.healthcare.iz.darq.digest.service.impl.PublicOnlyCryptoKey;
import gov.nist.healthcare.iz.darq.digest.service.patient.matching.mismo.MismoPatientMatchingService;
import gov.nist.healthcare.iz.darq.localreport.AvailableLocalReportServices;
import gov.nist.healthcare.iz.darq.localreport.LocalReportEngine;
import gov.nist.healthcare.iz.darq.digest.service.report.instances.BadPhoneNumberReportService;
import gov.nist.healthcare.iz.darq.digest.service.report.instances.DuplicateRecordsReportService;
import gov.nist.healthcare.iz.darq.digest.service.report.instances.LotNumberReportService;
import gov.nist.healthcare.iz.darq.digest.service.report.instances.PlaceholderNameReportService;
import gov.nist.healthcare.iz.darq.localreport.LocalReportService;
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
		ComplexDetectionProvider complexDetectionProvider = new ComplexDetectionProvider();
		VaccinationDuplicateDetectionProvider vaccinationDuplicateDetectionProvider = new VaccinationDuplicateDetectionProvider();
		Map<String, DetectionProvider> providers = new HashMap<>();
		providers.put(AvailableDetectionEngines.DP_ID_MQE, mqeDetectionProvider);
		providers.put(AvailableDetectionEngines.DP_ID_PM, mismoMatcherDetectionProvider);
		providers.put(AvailableDetectionEngines.DP_ID_VD, vaccinationDuplicateDetectionProvider);
		providers.put(AvailableDetectionEngines.DP_ID_COMPLEX_DETECTIONS, complexDetectionProvider);
		return new DetectionEngine(providers);
	}

	@Bean
	public LocalReportEngine reportEngine() {
		Map<String, LocalReportService> localReportServices = new HashMap<>();
		localReportServices.put(AvailableLocalReportServices.LR_BAD_PHONES, new BadPhoneNumberReportService());
		localReportServices.put(AvailableLocalReportServices.LR_DUPLICATE_RECORDS, new DuplicateRecordsReportService());
		localReportServices.put(AvailableLocalReportServices.LR_LOT_NUMBERS, new LotNumberReportService());
		localReportServices.put(AvailableLocalReportServices.LR_PLACEHOLDER_NAMES, new PlaceholderNameReportService());
		return new LocalReportEngine(localReportServices);
	}

	@Bean
	public ADFManager adfWriter() throws Exception {
		ADFManager manager = new ADFManager();
		manager.register(new BsonADFModule(), true, false);
		return manager;
	}

}
