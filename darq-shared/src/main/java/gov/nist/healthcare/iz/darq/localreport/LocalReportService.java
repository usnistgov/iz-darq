package gov.nist.healthcare.iz.darq.localreport;

import gov.nist.healthcare.iz.darq.detections.DetectionEngine;
import gov.nist.healthcare.iz.darq.detections.RecordDetectionEngineResult;
import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;
import org.apache.commons.csv.CSVFormat;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public abstract class LocalReportService {
	protected final String filename;
	protected boolean open;
	protected Path tempDirectory;
	protected Path outputDirectory;
	protected final CSVFormat csvFormat = CSVFormat.DEFAULT;
	protected FileWriter outputFileWriter;
	protected final Set<String> requiredDetectionDependencies;

	public LocalReportService(String filename) {
		this.filename = filename;
		this.requiredDetectionDependencies = new HashSet<>();
	}

	public LocalReportService(String filename, String ...requiredDetectionDependencies) {
		this.filename = filename;
		this.requiredDetectionDependencies = Arrays.stream(requiredDetectionDependencies).collect(Collectors.toSet());
	}

	public abstract List<String> getHeader();
	public abstract void onOpen() throws Exception;
	public abstract void onClose() throws Exception;

	public boolean handleInclude(LocalReportEngineConfiguration configuration, DetectionEngine detectionEngine) {
		return true;
	}

	public boolean dependenciesAreMet(DetectionEngine detectionEngine) {
		return this.getRequiredDetectionDependencies().isEmpty() || this.getRequiredDetectionDependencies().stream()
		                  .anyMatch(detectionEngine.getActiveDetectionCodes()::contains);
	}

	public boolean include(LocalReportEngineConfiguration configuration, DetectionEngine detectionEngine) {
		return dependenciesAreMet(detectionEngine) && handleInclude(configuration, detectionEngine);
	}

	public void open(Path tempDirectory, Path outputDirectory) throws Exception {
		if(this.open) {
			throw new Exception(this.filename + " report file is already open");
		}
		this.tempDirectory = tempDirectory;
		this.outputDirectory = outputDirectory;
		this.open = true;
		this.onOpen();
	}

	public void close() throws Exception {
		if(!this.open) {
			throw new Exception(this.filename + " report file is not open");
		}
		this.onClose();
		this.open = false;
	}

	public abstract void process(PreProcessRecord record, RecordDetectionEngineResult recordDetectionEngineResult) throws Exception;

	public void openOutputFile() throws Exception {
		if(outputFileWriter == null) {
			outputFileWriter = new FileWriter(Paths.get(this.outputDirectory.toAbsolutePath().toString(), filename).toFile());
		} else {
			throw new Exception("Output file "+ filename +" already open.");
		}
	}

	public void closeOutputFile() throws IOException {
		outputFileWriter.close();
	}

	public String getFilename() {
		return filename;
	}

	public Set<String> getRequiredDetectionDependencies() {
		return Collections.unmodifiableSet(requiredDetectionDependencies);
	}
}
