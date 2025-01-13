package gov.nist.healthcare.iz.darq.detections;

import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;

import java.util.List;
import java.util.Set;

public interface DetectionProvider {
	/*  "List<DetectionProvider> before" the list of Detection Providers registered in the Detection Engine before this one
		This is necessary because the ComplexDetection provider's configuration depends on the previous detection providers */
	void configure(DetectionEngineConfiguration configuration, List<DetectionProvider> before) throws Exception;
	boolean include(DetectionEngineConfiguration configuration);
	Set<DetectionDescriptor> getDetections();
	void close() throws Exception;
	void process(PreProcessRecord record, DetectionContext context, RecordDetectionEngineResult detections) throws Exception;
}
