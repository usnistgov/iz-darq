package gov.nist.healthcare.iz.darq.detections;

import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;

import java.util.ArrayList;
import java.util.List;

public class DetectionEngineConfiguration {
	ConfigurationPayload configurationPayload;
	List<String> activeProviders = new ArrayList<>();
	String temporaryDirectory;
	String outputDirectory;

	public ConfigurationPayload getConfigurationPayload() {
		return configurationPayload;
	}

	public void setConfigurationPayload(ConfigurationPayload configurationPayload) {
		this.configurationPayload = configurationPayload;
	}

	public void addActiveProvider(String providerId) {
		activeProviders.add(providerId);
	}

	public List<String> getActiveProviders() {
		return activeProviders;
	}

	public void setActiveProviders(List<String> activeProviders) {
		this.activeProviders = activeProviders;
	}

	public String getTemporaryDirectory() {
		return temporaryDirectory;
	}

	public void setTemporaryDirectory(String temporaryDirectory) {
		this.temporaryDirectory = temporaryDirectory;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}
}
