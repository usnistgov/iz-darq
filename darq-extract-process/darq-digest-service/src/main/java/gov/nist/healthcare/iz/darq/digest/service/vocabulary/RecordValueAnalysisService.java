package gov.nist.healthcare.iz.darq.digest.service.vocabulary;

import gov.nist.healthcare.iz.darq.detections.DetectionContext;
import gov.nist.healthcare.iz.darq.digest.domain.TablePayload;
import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;

import java.util.Map;

public interface RecordValueAnalysisService {
	RecordValuesAnalysisResult analyseRecordValues(PreProcessRecord record) throws IllegalAccessException;

	// Provider, Age Group, Year, Gender, Source, Code, Count
	Map<String, Map<String, Map<String, Map<String, Map<String, TablePayload>>>>> getVaccinationEvents(PreProcessRecord record, DetectionContext context);
}
