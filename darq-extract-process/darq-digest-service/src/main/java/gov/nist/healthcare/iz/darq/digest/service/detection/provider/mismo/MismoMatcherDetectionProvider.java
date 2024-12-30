package gov.nist.healthcare.iz.darq.digest.service.detection.provider.mismo;

import com.google.common.base.Strings;
import gov.nist.healthcare.iz.darq.detections.*;
import gov.nist.healthcare.iz.darq.digest.domain.DetectionSum;
import gov.nist.healthcare.iz.darq.digest.service.detection.provider.mqe.MQEDetectionProvider;
import gov.nist.healthcare.iz.darq.digest.service.patient.matching.mismo.MismoPatientMatchingService;
import gov.nist.healthcare.iz.darq.patient.matching.model.PatientMatchingDetection;
import gov.nist.healthcare.iz.darq.patient.matching.model.RecordMatchProcessResult;
import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;
import org.immregistries.mqe.vxu.VxuObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class MismoMatcherDetectionProvider implements DetectionProvider {

	MismoPatientMatchingService patientMatchingService;
	private final Set<DetectionDescriptor> descriptors = new HashSet<>();
	private final static Logger logger = LoggerFactory.getLogger(MQEDetectionProvider.class.getName());
	private final static String PM001 = PatientMatchingDetection.PM001.name();

	public MismoMatcherDetectionProvider(MismoPatientMatchingService patientMatchingService) {
		descriptors.add(new DetectionDescriptor(PatientMatchingDetection.PM001.name(), PatientMatchingDetection.PM001.getMessage(), VxuObject.PATIENT.name(), true));
		this.patientMatchingService = patientMatchingService;
	}

	@Override
	public void configure(DetectionEngineConfiguration configuration) throws Exception {
		logger.info("Configuring MISMO Matching Detection Provider");
		this.patientMatchingService.initialize(Paths.get(configuration.getTemporaryDirectory()), Paths.get(configuration.getOutputDirectory()));
		if(!Strings.isNullOrEmpty(configuration.getConfigurationPayload().getMismoPatientMatchingConfiguration())) {
			this.patientMatchingService.configure(configuration.getConfigurationPayload().getMismoPatientMatchingConfiguration());
		}
	}

	@Override
	public boolean hasSideEffect() {
		// Matches patients records and creates patient_matches.tsv file
		return true;
	}

	@Override
	public Set<String> getEnabledDetectionCodes() {
		return provides().stream().map(DetectionDescriptor::getCode).collect(Collectors.toSet());
	}

	@Override
	public Set<DetectionDescriptor> provides() {
		return Collections.unmodifiableSet(descriptors);
	}

	@Override
	public void close() throws Exception {
		logger.info("Closing patient matching service resources");
		patientMatchingService.close();
	}

	@Override
	public RecordDetectionEngineResult processRecordAndGetDetections(PreProcessRecord record, DetectionContext context) throws Exception {
		RecordMatchProcessResult matchProcessResult = patientMatchingService.process(record.getRecord());
		RecordDetectionEngineResult detections = new RecordDetectionEngineResult();
		detections.setPossiblePatientRecordDuplicatesWithSignature(matchProcessResult.getDuplicatesWithSignature());
		if(context.keepDetection(PM001)) {
			Map<String, DetectionSum> patient = new HashMap<>();
			detections.setPatientDetections(patient);
			if(matchProcessResult.isDuplicate()) {
				patient.put(PM001, new DetectionSum(0, 1));
			} else {
				patient.put(PM001, new DetectionSum(1, 0));
			}
		}
		return detections;
	}
}
