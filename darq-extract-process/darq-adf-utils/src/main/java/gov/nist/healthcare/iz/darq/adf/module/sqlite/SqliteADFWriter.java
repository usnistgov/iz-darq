package gov.nist.healthcare.iz.darq.adf.module.sqlite;
import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import gov.nist.healthcare.iz.darq.adf.module.archive.ADFArchiveManager;
import gov.nist.healthcare.iz.darq.adf.module.sqlite.model.Dictionaries;
import gov.nist.healthcare.iz.darq.digest.domain.*;

import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;


public class SqliteADFWriter extends SimpleADFWriter {
	private String ADF_SQLITE_TEMP;
	private String DB_LOCATION;
	private Connection connection;
	private int write_no = 1;
	private final int COMMIT_SIZE = 100;
	private final CryptoKey key;
	private SqliteADFDAO dao;

	protected Dictionaries dictionaries = new Dictionaries();

	public SqliteADFWriter(CryptoKey key) {
		this.key = key;
	}

	@Override
	public void open(String temporaryDirectory) throws Exception {
		try {
			this.ADF_SQLITE_TEMP = UUID.randomUUID() + ".db";
			this.DB_LOCATION = temporaryDirectory + "/" + ADF_SQLITE_TEMP;
			connection = DriverManager.getConnection("jdbc:sqlite:" + DB_LOCATION);
			create();
			dao = new SqliteADFDAO(connection);
			connection.setAutoCommit(false);
		} catch (Exception e){
			close();
			throw e;
		}
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
		dictionaries = null;
		Files.deleteIfExists(Paths.get(this.DB_LOCATION));
		if(this.connection != null && !this.connection.isClosed()) {
			this.connection.close();
		}
	}

	protected void writeMetaAndEncode() throws Exception {
		// COMMIT ANY PENDING TRANSACTION
		connection.commit();

		SecretKeySpec secret = crypto.generateAESKey();
		// WRITE METADATA
		dao.write_metadata(
				this.metadata,
				this.getSummary(),
				this.configurationPayload,
				getVersion(),
				secret
		);

		// WRITE DICTIONARIES
		dao.write_dictionaries(dictionaries, secret);

		// WRITE SECURITY
		dao.write_sec(key, secret);

		// COMMIT
		connection.commit();
	}

	@Override
	public void exportAndClose(String location) throws Exception {
		writeMetaAndEncode();
		try {
			connection.setAutoCommit(true);
			this.connection.createStatement().execute("VACUUM ");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		// CREATE ARCHIVE AND COPY TO LOCATION
		ADFArchiveManager.getInstance().create(this.DB_LOCATION, location, getVersion());
		this.close();
	}

	@Override
	public ADFVersion getVersion() {
		return ADFVersion.ADFSQLITE0001;
	}

	@Override
	public String getAsString() throws Exception {
		throw new Exception("ADFWriter for ADF Version '" + getVersion() + "' does not support print");
	}

	@Override
	public boolean supportsPrint() {
		return false;
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
		dao.write_p_detections(
				dictionaries.getId(Field.AGE_GROUP, ageGroup),
				dictionaries.getId(Field.DETECTION, code),
				p,
				n
		);
	}

	protected void write_p_vocab(String ageGroup, String table, String code, int nb) throws SQLException {
		dao.write_p_vocab(
				dictionaries.getId(Field.AGE_GROUP, ageGroup),
				dictionaries.getId(Field.TABLE, table),
				dictionaries.getId(Field.CODE, code),
				nb
		);
	}

	protected void provider_detections(String provider, String ageGroup, String code, int p, int n, boolean patient) throws SQLException {
		dao.write_provider_detections(
				dictionaries.getId(Field.PROVIDER, provider),
				dictionaries.getId(Field.AGE_GROUP, ageGroup),
				dictionaries.getId(Field.DETECTION, code),
				p,
				n,
				patient
		);
	}

	protected void provider_vocab(String provider, String ageGroup, String table, String code, int nb, boolean patient) throws SQLException {
		dao.write_provider_vocab(
				dictionaries.getId(Field.PROVIDER, provider),
				dictionaries.getId(Field.AGE_GROUP, ageGroup),
				dictionaries.getId(Field.TABLE, table),
				dictionaries.getId(Field.CODE, code),
				nb,
				patient
		);
	}

	protected void write_v_events(String provider, String ageGroup, String year, String gender, String source, String code, int nb) throws SQLException {
		dao.write_v_events(
				dictionaries.getId(Field.PROVIDER, provider),
				dictionaries.getId(Field.AGE_GROUP, ageGroup),
				dictionaries.getId(Field.VACCINATION_YEAR, year),
				dictionaries.getId(Field.GENDER, gender),
				dictionaries.getId(Field.EVENT, source),
				dictionaries.getId(Field.VACCINE_CODE, code),
				nb
		);
	}
}
