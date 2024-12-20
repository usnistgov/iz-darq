package gov.nist.healthcare.iz.darq.digest.service.report;

import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.util.List;

public abstract class SimpleLocalReportService extends LocalReportService {

	private CSVPrinter printer;

	public SimpleLocalReportService(String filename) {
		super(filename);
	}

	@Override
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
