package gov.nist.healthcare.iz.darq.localreport;

import gov.nist.healthcare.iz.darq.detections.RecordDetectionEngineResult;
import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.util.List;

public abstract class SimpleLocalReportService extends LocalReportService {

	private CSVPrinter printer;

	public SimpleLocalReportService(String filename) {
		super(filename);
	}

	public SimpleLocalReportService(String filename, String ...requiredDetectionDependencies) {
		super(filename, requiredDetectionDependencies);
	}

	public void process(PreProcessRecord record, RecordDetectionEngineResult recordDetectionEngineResult) throws Exception {
		List<List<String>> rows = getRows(record, recordDetectionEngineResult);
		if(rows != null && !rows.isEmpty()) {
			for(List<String> row : rows) {
				write(row);
			}
		}
	}

	public abstract List<List<String>> getRows(PreProcessRecord record, RecordDetectionEngineResult recordDetectionEngineResult);

	public void write(List<String> row) throws IOException {
		printer.printRecord(row);
	}

	@Override
	public void onOpen() throws Exception {
		this.openOutputFile();
		printer = new CSVPrinter(this.outputFileWriter, csvFormat);
		printer.printRecord(this.getHeader());
	}

	@Override
	public void onClose() throws IOException {
		printer.close();
	}
}
