package gov.nist.healthcare.iz.darq.localreport;

import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;

import java.util.ArrayList;
import java.util.List;

public class LocalReportEngineConfiguration {
	ConfigurationPayload configurationPayload;
	List<String> activeReportEngines = new ArrayList<>();
	String temporaryDirectory;
	String outputDirectory;

	public List<String> getActiveReportEngines() {
		return activeReportEngines;
	}

	public void addActiveLocalReportEngine(String localReportEngineId) {
		activeReportEngines.add(localReportEngineId);
	}


	public void setActiveReportEngines(List<String> activeReportEngines) {
		this.activeReportEngines = activeReportEngines;
	}

	public ConfigurationPayload getConfigurationPayload() {
		return configurationPayload;
	}

	public void setConfigurationPayload(ConfigurationPayload configurationPayload) {
		this.configurationPayload = configurationPayload;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public String getTemporaryDirectory() {
		return temporaryDirectory;
	}

	public void setTemporaryDirectory(String temporaryDirectory) {
		this.temporaryDirectory = temporaryDirectory;
	}
}
