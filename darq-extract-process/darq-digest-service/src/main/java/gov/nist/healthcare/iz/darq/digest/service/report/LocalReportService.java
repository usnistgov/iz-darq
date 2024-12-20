package gov.nist.healthcare.iz.darq.digest.service.report;

import gov.nist.healthcare.iz.darq.detections.AggregatedRecordDetections;
import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;
import org.apache.commons.csv.CSVFormat;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public abstract class LocalReportService {
	protected final String filename;
	protected boolean open;
	protected Path tempDirectory;
	protected Path outputDirectory;
	protected final CSVFormat csvFormat = CSVFormat.DEFAULT;
	protected FileWriter outputFileWriter;

	public LocalReportService(String filename) {
		this.filename = filename;
	}

	public abstract List<String> getHeader();
	public abstract List<List<String>> getRows(AggregatePatientRecord patientRecord, AggregatedRecordDetections detections);
	public abstract void write(List<String> row) throws Exception;
	public abstract void onOpen() throws Exception;
	public abstract void onClose() throws Exception;

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

	public void process(AggregatePatientRecord patientRecord, AggregatedRecordDetections detections) throws Exception {
		List<List<String>> rows = getRows(patientRecord, detections);
		if(rows != null && !rows.isEmpty()) {
			for(List<String> row : rows) {
				write(row);
			}
		}
	}

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
}
