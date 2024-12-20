package gov.nist.healthcare.iz.darq.digest.service.report;


import gov.nist.healthcare.iz.darq.adf.module.sqlite.SqliteADFWriter;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;
import java.util.Objects;

public abstract class AggregateLocalReportService extends LocalReportService {

	private Connection connection;
	protected String dbFileLocation;
	protected PreparedStatement writeValue;
	protected PreparedStatement readValue;
	protected final int COMMIT_SIZE = 200;
	protected int write_no;

	public AggregateLocalReportService(String filename) {
		super(filename);
	}

	@Override
	public void write(List<String> row) throws Exception {
		String value = getRowString(row);
		this.writeValue.setString(1, value);
		this.writeValue.setInt(2, 1);
		this.writeValue.setInt(3, 1);
		this.writeValue.execute();

		if((write_no % COMMIT_SIZE) == 0) {
			connection.commit();
		}
		write_no++;
	}

	public String getRowString(List<String> row) throws IOException {
		StringWriter value = new StringWriter();
		CSVPrinter csvPrinter = new CSVPrinter(value , csvFormat);
		csvPrinter.printRecord(row);
		return value.toString();
	}

	@Override
	public void onOpen() throws Exception {
		try {
			this.openOutputFile();
			String dbFilename = this.filename + ".db";
			dbFileLocation = Paths.get(this.tempDirectory.toAbsolutePath().toString(), dbFilename).toAbsolutePath().toString();
			connection = DriverManager.getConnection("jdbc:sqlite:" + dbFileLocation);
			create();
			connection.setAutoCommit(false);
			this.writeValue = connection.prepareStatement("INSERT INTO REPORT(?, ?) ON CONFLICT DO UPDATE SET N=N+?");
			this.readValue = connection.prepareStatement("SELECT * FROM REPORT");
		} catch (Exception e){
			close();
			throw e;
		}
	}

	void create() throws SQLException {
		Statement statement = connection.createStatement();
		statement.executeQuery("PRAGMA journal_mode=OFF");
		statement.execute("PRAGMA synchronous=OFF ");
		new BufferedReader(new InputStreamReader(
				Objects.requireNonNull(SqliteADFWriter.class.getResourceAsStream("/local_report_schema.sql")),
				StandardCharsets.UTF_8)
		).lines().forEach((line) -> {
			try {
				statement.execute(line);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
	}

	private void writeFile() throws SQLException, IOException {
		ResultSet result = readValue.executeQuery();
		while(result.next()) {
			String value = result.getString("VALUE");
			int number = result.getInt("N");
			this.outputFileWriter.append(value).append(", ").append(String.valueOf(number)).append("\n");
		}
		result.close();
	}

	@Override
	public void onClose() throws Exception {
		connection.commit();
		writeFile();
		this.connection.close();
		this.closeOutputFile();
	}
}
