package gov.nist.healthcare.iz.darq.detections;

import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DetectionEngine {
	private final Map<String, DetectionProvider> providers;
	private final Set<DetectionProvider> activeDetectionProviders;

	public DetectionEngine(Map<String, DetectionProvider> providers) {
		this.providers = providers;
		this.activeDetectionProviders = new HashSet<>();
	}

	public Set<String> getActiveDetectionCodes() {
		return activeDetectionProviders.stream()
				.flatMap(provider -> provider.getEnabledDetectionCodes().stream())
				.collect(Collectors.toSet());
	}

	public void configure(DetectionEngineConfiguration configuration) throws Exception {
		activeDetectionProviders.clear();
		for(String providerId: configuration.getActiveProviders()) {
			if(providers.containsKey(providerId)) {
				DetectionProvider  provider = providers.get(providerId);
				boolean isInConfiguration = provider.getEnabledDetectionCodes().stream().anyMatch((pCode) -> configuration.getDetections().contains(pCode));
				if(isInConfiguration || provider.hasSideEffect()) {
					provider.configure(configuration);
					activeDetectionProviders.add(provider);
				}
			}
		}
	}

	public AggregatedRecordDetections processRecordAndGetDetections(PreProcessRecord record, DetectionContext context) throws Exception {
		AggregatedRecordDetections detections = new AggregatedRecordDetections();
		detections.setPatient(new HashMap<>());
		detections.setVaccinations(new HashMap<>());

		for(DetectionProvider provider: activeDetectionProviders) {
			detections.merge(provider.processRecordAndGetDetections(record, context));
		}

		return detections;
	}

	public void close() throws Exception {
		for(DetectionProvider provider: providers.values()) {
			provider.close();
		}
	}



}
