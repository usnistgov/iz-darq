package gov.nist.healthcare.iz.darq.adf.writer.sqlite;
import gov.nist.healthcare.iz.darq.adf.writer.Dictionary;
import gov.nist.healthcare.iz.darq.adf.writer.SimpleADFWriter;
import gov.nist.healthcare.iz.darq.digest.domain.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

public class SqliteADFWriter extends SimpleADFWriter {

	private final String SUPPORTED_ADF_VERSION = "SQLITE_V01";
	private final String ADF_SQLITE_TEMP = "temporary_adf_write.sql";
	private Connection connection;
	private PreparedStatement P_DETECTIONS;
	private PreparedStatement P_VOCAB;
	private PreparedStatement P_PROVIDER_DETECTIONS;
	private PreparedStatement P_PROVIDER_VOCAB;
	private PreparedStatement V_DETECTIONS;
	private PreparedStatement V_VOCAB;
	private PreparedStatement V_EVENTS;
	private int write_no = 1;
	private final int COMMIT_SIZE = 100;

	protected Dictionary PROVIDERS = new Dictionary();
	protected Dictionary AGE_GROUP = new Dictionary();
	protected Dictionary TABLE = new Dictionary();
	protected Dictionary DETECTION = new Dictionary();
	protected Dictionary CVX = new Dictionary();
	protected Dictionary GENDER = new Dictionary();
	protected Dictionary YEAR = new Dictionary();
	protected Dictionary SOURCE = new Dictionary();
	protected Dictionary CODE = new Dictionary();

	@Override
	public void open(String temporaryDirectory) throws Exception {
		connection = DriverManager.getConnection("jdbc:sqlite:" + temporaryDirectory + "/" + ADF_SQLITE_TEMP);
		create();
		connection.setAutoCommit(false);
		this.P_DETECTIONS = connection.prepareStatement("INSERT INTO P_DETECTIONS VALUES(?, ?, ?, ?) ON CONFLICT DO UPDATE SET P=P+?, N=N+?");
		this.V_DETECTIONS = connection.prepareStatement("INSERT INTO V_DETECTIONS VALUES(?, ?, ?, ?, ?) ON CONFLICT DO UPDATE SET P=P+?, N=N+?");
		this.P_PROVIDER_DETECTIONS = connection.prepareStatement("INSERT INTO P_PROVIDER_DETECTIONS VALUES(?, ?, ?, ?, ?) ON CONFLICT DO UPDATE SET P=P+?, N=N+?");
		this.P_VOCAB = connection.prepareStatement("INSERT INTO P_VOCAB VALUES(?, ?, ?, ?) ON CONFLICT DO UPDATE SET N=N+?");
		this.V_VOCAB = connection.prepareStatement("INSERT INTO V_VOCAB VALUES(?, ?, ?, ?, ?) ON CONFLICT DO UPDATE SET N=N+?");
		this.P_PROVIDER_VOCAB = connection.prepareStatement("INSERT INTO P_PROVIDER_VOCAB VALUES(?, ?, ?, ?, ?) ON CONFLICT DO UPDATE SET N=N+?");
		this.V_EVENTS = connection.prepareStatement("INSERT INTO V_EVENTS VALUES(?, ?, ?, ?, ?, ?, ?) ON CONFLICT DO UPDATE SET N=N+?");
	}



	void create() throws SQLException {
		Statement statement = connection.createStatement();
		statement.executeQuery("PRAGMA journal_mode=OFF");
		statement.execute("PRAGMA synchronous=OFF ");
		new BufferedReader(new InputStreamReader(Objects.requireNonNull(SqliteADFWriter.class.getResourceAsStream("/adf_schema.sql")),StandardCharsets.UTF_8)).lines().forEach((line) -> {
			try {
				statement.execute(line);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		});
	}

	@Override
	public void close() throws Exception {
		connection.commit();
		this.connection.close();
	}

	@Override
	public void export(String location) throws Exception {
		this.connection.createStatement().execute("VACUUM ");

	}

	@Override
	public String getSupportedADFVersion() {
		return SUPPORTED_ADF_VERSION;
	}

	@Override
	public void write(ADChunk chunk) throws Exception {
		super.write(chunk);
		if((write_no % COMMIT_SIZE) == 0) {
			connection.commit();
		}
		write_no++;
	}

	protected void write_p_detections(String ageGroup, String code, int p, int n) throws SQLException {
		this.P_DETECTIONS.setInt(1, AGE_GROUP.getId(ageGroup));
		this.P_DETECTIONS.setInt(2, DETECTION.getId(code));
		this.P_DETECTIONS.setInt(3, p);
		this.P_DETECTIONS.setInt(4, n);
		this.P_DETECTIONS.setInt(5, p);
		this.P_DETECTIONS.setInt(6, n);
		this.P_DETECTIONS.execute();
	}

	protected void write_p_vocab(String ageGroup, String table, String code, int nb) throws SQLException {
		this.P_VOCAB.setInt(1, AGE_GROUP.getId(ageGroup));
		this.P_VOCAB.setInt(2, TABLE.getId(table));
		this.P_VOCAB.setInt(3, CODE.getId(code));
		this.P_VOCAB.setInt(4, nb);
		this.P_VOCAB.setInt(5, nb);
		this.P_VOCAB.execute();
	}

	protected void provider_detections(String provider, String ageGroup, String code, int p, int n, boolean patient) throws SQLException {
		PreparedStatement STAT = patient ? this.P_PROVIDER_DETECTIONS : this.V_DETECTIONS;
		STAT.setInt(1, PROVIDERS.getId(provider));
		STAT.setInt(2, AGE_GROUP.getId(ageGroup));
		STAT.setInt(3, DETECTION.getId(code));
		STAT.setInt(4, p);
		STAT.setInt(5, n);
		STAT.setInt(6, p);
		STAT.setInt(7, n);
		STAT.execute();
	}

	protected void provider_vocab(String provider, String ageGroup, String table, String code, int nb, boolean patient) throws SQLException {
		PreparedStatement STAT = patient ? this.P_PROVIDER_VOCAB : this.V_VOCAB;
		STAT.setInt(1, PROVIDERS.getId(provider));
		STAT.setInt(2, AGE_GROUP.getId(ageGroup));
		STAT.setInt(3, TABLE.getId(table));
		STAT.setInt(4, CODE.getId(code));
		STAT.setInt(5, nb);
		STAT.setInt(6, nb);
		STAT.execute();
	}

	protected void write_v_events(String provider, String ageGroup, String year, String gender, String source, String code, int nb) throws SQLException {
		this.V_EVENTS.setInt(1, PROVIDERS.getId(provider));
		this.V_EVENTS.setInt(2, AGE_GROUP.getId(ageGroup));
		this.V_EVENTS.setInt(3, YEAR.getId(year));
		this.V_EVENTS.setInt(4, GENDER.getId(gender));
		this.V_EVENTS.setInt(5, SOURCE.getId(source));
		this.V_EVENTS.setInt(6, CVX.getId(code));
		this.V_EVENTS.setInt(7, nb);
		this.V_EVENTS.setInt(8, nb);
		this.V_EVENTS.execute();
	}
}
