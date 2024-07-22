package gov.nist.healthcare.iz.darq.adf.module.sqlite;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import gov.nist.healthcare.iz.darq.adf.module.archive.ADFArchiveManager;
import gov.nist.healthcare.iz.darq.adf.module.sqlite.model.Dictionaries;
import gov.nist.healthcare.iz.darq.digest.domain.*;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
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
		if(this.connection != null && !this.connection.isClosed()) {
			this.connection.close();
		}
		Files.deleteIfExists(Paths.get(this.DB_LOCATION));
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
	public void writeAsString(FileWriter writer) throws Exception {
		// COMMIT ANY PENDING TRANSACTION
		connection.commit();

		DatabaseMetaData meta = connection.getMetaData();
		JsonFactory jsonFactory = new JsonFactory();
		jsonFactory.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
		ObjectMapper mapper = new ObjectMapper(jsonFactory);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		ResultSet tables = meta.getTables("", "", "%", new String[]{"TABLE"});

		writer.write("** Table : METADATA (Encrypted Content) **\n");
		writer.write("* INFO\n");
		mapper.writeValue(writer, metadata);
		writer.write("\n\n");
		writer.write("* SUMMARY\n");
		mapper.writeValue(writer, summary);
		writer.write("\n\n");
		writer.write("* CONFIGURATION\n");
		mapper.writeValue(writer, configurationPayload);
		writer.write("\n\n");
		writer.write("* VERSION : "+ getVersion().name());
		writer.write("\n\n");

		writer.write("** Table : DICTIONARY (Encrypted Content) **\n");
		for(Field field: dictionaries.get().keySet()) {
			writer.write("* "+field.name()+"\n");
			mapper.writeValue(writer, dictionaries.getValues(field));
			writer.write("\n\n");
		}

		writer.write("** Table : SEC **\n");
		writer.write("* KEY : Contains encrypted AES key using qDAR public key. \nthe AES secret key will be generated when the final ADF is written into disk.\n");
		writer.write("* PUBLIC KEY HASH : "+  DatatypeConverter.printHexBinary(key.getPublicKeyHash()));

		while(tables.next()) {
			String name = tables.getString("TABLE_NAME");
			if(!Arrays.asList("METADATA", "SEC", "DICTIONARY").contains(name)) {
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery("SELECT * FROM "+ name);
				int columns = rs.getMetaData().getColumnCount();
				writer.write("\n\n");
				writer.write("** Table : " + name +" **");
				writer.write("\n");
				List<Integer> tabSize = new ArrayList<>();
				for(int i = 1; i <= columns; i++) {
					int size = rs.getMetaData().getColumnName(i).length();
					tabSize.add((size / 6) + 1);
					writer.write(rs.getMetaData().getColumnName(i)+"\t| ");
				}
				writer.write("\n");
				while(rs.next()) {
					for(int i = 1; i <= columns; i++) {
						writer.write(rs.getString(i));
						for(int j = 0; j < tabSize.get(i - 1); j++) {
							writer.write("\t");
						}
						writer.write("| ");
					}
					writer.write("\n");
				}
			}
		}
	}

	@Override
	public boolean supportsPrint() {
		return true;
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
