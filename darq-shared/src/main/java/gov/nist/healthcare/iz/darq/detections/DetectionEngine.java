package gov.nist.healthcare.iz.darq.detections;

import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;

import java.util.*;
import java.util.stream.Collectors;

public class DetectionEngine {
	private final Map<String, DetectionProvider> providers;
	private final List<DetectionProvider> activeDetectionProviders;

	public DetectionEngine(Map<String, DetectionProvider> providers) {
		this.providers = providers;
		this.activeDetectionProviders = new ArrayList<>();
	}

	public Set<String> getActiveDetectionCodes() {
		return activeDetectionProviders.stream()
		                               .flatMap(provider -> provider.getDetections()
		                                                            .stream()
		                                                            .map(DetectionDescriptor::getCode)
		                               )
		                               .collect(Collectors.toSet());
	}

	public void configure(DetectionEngineConfiguration configuration) throws Exception {
		activeDetectionProviders.clear();
		for(String providerId: configuration.getActiveProviders()) {
			if(providers.containsKey(providerId)) {
				DetectionProvider  provider = providers.get(providerId);
				if(provider.include(configuration)) {
					provider.configure(configuration, Collections.unmodifiableList(activeDetectionProviders));
					activeDetectionProviders.add(provider);
				}
			}
		}
	}

	public RecordDetectionEngineResult process(PreProcessRecord record, DetectionContext context) throws Exception {
		RecordDetectionEngineResult detections = new RecordDetectionEngineResult();

		for(DetectionProvider provider: activeDetectionProviders) {
			provider.process(record, context, detections);
		}

		return detections;
	}

	public boolean isDetectionProviderActive(String providerId) {
		return activeDetectionProviders.contains(providers.get(providerId));
	}

	public void close() throws Exception {
		for(DetectionProvider provider: providers.values()) {
			provider.close();
		}
	}



}
