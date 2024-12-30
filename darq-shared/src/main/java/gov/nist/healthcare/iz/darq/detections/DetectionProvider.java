package gov.nist.healthcare.iz.darq.detections;

import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;

import java.util.Set;

public interface DetectionProvider {
	void configure(DetectionEngineConfiguration configuration) throws Exception;
	boolean hasSideEffect();
	Set<String> getEnabledDetectionCodes();
	Set<DetectionDescriptor> provides();
	void close() throws Exception;
	RecordDetectionEngineResult processRecordAndGetDetections(PreProcessRecord record, DetectionContext context) throws Exception;
}
