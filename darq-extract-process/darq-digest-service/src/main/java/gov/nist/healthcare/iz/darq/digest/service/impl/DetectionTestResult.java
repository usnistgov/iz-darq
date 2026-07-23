package gov.nist.healthcare.iz.darq.digest.service.impl;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class DetectionTestResult {

	public static class Summary {
		private int recordsEvaluated;
		private int detected;
		private int notDetected;
		private int notEvaluable;

		public void add(DetectionTestResultRow.Result result) {
			recordsEvaluated++;
			switch(result) {
				case DETECTED:
					detected++;
					break;
				case NOT_DETECTED:
					notDetected++;
					break;
				case NOT_EVALUABLE:
					notEvaluable++;
					break;
			}
		}

		public int getRecordsEvaluated() {
			return recordsEvaluated;
		}

		public int getDetected() {
			return detected;
		}

		public int getNotDetected() {
			return notDetected;
		}

		public int getNotEvaluable() {
			return notEvaluable;
		}
	}

	private final Path outputDirectory;
	private final Path detailsFile;
	private final Map<String, Summary> summaries = new LinkedHashMap<>();

	public DetectionTestResult(Path outputDirectory, Path detailsFile) {
		this.outputDirectory = outputDirectory;
		this.detailsFile = detailsFile;
	}

	public void add(String detection, DetectionTestResultRow.Result result) {
		if(!summaries.containsKey(detection)) {
			summaries.put(detection, new Summary());
		}
		summaries.get(detection).add(result);
	}

	public Path getOutputDirectory() {
		return outputDirectory;
	}

	public Path getDetailsFile() {
		return detailsFile;
	}

	public Map<String, Summary> getSummaries() {
		return summaries;
	}
}
