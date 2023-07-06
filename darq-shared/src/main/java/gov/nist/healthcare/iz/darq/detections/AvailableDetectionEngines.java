package gov.nist.healthcare.iz.darq.detections;

import gov.nist.healthcare.iz.darq.patient.matching.model.PatientMatchingDetection;
import org.immregistries.mqe.validator.detection.Detection;
import org.immregistries.mqe.validator.detection.MqeCode;
import org.immregistries.mqe.validator.engine.rules.ValidationRuleEntityLists;
import org.immregistries.mqe.vxu.TargetType;
import org.immregistries.mqe.vxu.VxuObject;

import java.util.*;
import java.util.stream.Collectors;

public class AvailableDetectionEngines {
	public static final String DP_ID_MQE = "MQE";
	public static final String DP_ID_PM = "PATIENT_MATCHER";
	public static final Map<String, Set<String>> CODES_BY_DETECTION_PROVIDER = new HashMap<String, Set<String>>() {{
		put(DP_ID_MQE, Arrays.stream(MqeCode.values()).map((MqeCode::name)).collect(Collectors.toSet()));
		put(DP_ID_PM, Arrays.stream(PatientMatchingDetection.values()).map((PatientMatchingDetection::name)).collect(Collectors.toSet()));
	}};
	public static final Set<String> ALL_DETECTION_CODES = CODES_BY_DETECTION_PROVIDER.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
	public static final Set<DetectionDescriptor> ALL_DETECTION_DESCRIPTORS = new HashSet<DetectionDescriptor>() {{
		Set<Detection> all = new HashSet<>(Arrays.asList(Detection.values()));
		Set<Detection> active =  ValidationRuleEntityLists.activeDetectionsForTargets(new HashSet<>(Arrays.asList(
				TargetType.Patient,
				TargetType.NextOfKin,
				TargetType.Vaccination
		)));
		for(Detection d : all) {
			this.add(new DetectionDescriptor(d.getMqeMqeCode(), d.getDisplayText(),d.getTargetObject().toString(), active.contains(d)));
		}

		// Patient Matching Detections
		this.add(new DetectionDescriptor(PatientMatchingDetection.PM001.name(), PatientMatchingDetection.PM001.getMessage(), VxuObject.PATIENT.name(), true));
	}};
}
