package gov.nist.healthcare.iz.darq.detections;

import java.util.HashSet;
import java.util.Set;

public class DetectionEngineConfiguration {
	Set<String> detections;
	Set<String> activeProviders = new HashSet<>();
	String temporaryDirectory;
	String outputDirectory;

	public Set<String> getDetections() {
		return detections;
	}

	public void setDetections(Set<String> detections) {
		this.detections = detections;
	}

	public void addActiveProvider(String providerId) {
		activeProviders.add(providerId);
	}

	public Set<String> getActiveProviders() {
		return activeProviders;
	}

	public void setActiveProviders(Set<String> activeProviders) {
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
