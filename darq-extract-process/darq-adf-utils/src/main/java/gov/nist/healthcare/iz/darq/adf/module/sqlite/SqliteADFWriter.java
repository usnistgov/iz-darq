package gov.nist.healthcare.iz.darq.adf.module.sqlite;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import gov.nist.healthcare.iz.darq.adf.module.sqlite.model.Dictionaries;
import gov.nist.healthcare.iz.darq.digest.domain.*;
import org.apache.commons.io.IOUtils;

import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SqliteADFWriter extends SimpleADFWriter {
	private final String ADF_SQLITE_TEMP = "temporary_adf_write.sql";
	private String DB_LOCATION;
	private Connection connection;
	private PreparedStatement P_DETECTIONS;
	private PreparedStatement P_VOCAB;
	private PreparedStatement P_PROVIDER_DETECTIONS;
	private PreparedStatement P_PROVIDER_VOCAB;
	private PreparedStatement V_DETECTIONS;
	private PreparedStatement V_VOCAB;
	private PreparedStatement V_EVENTS;
	private PreparedStatement METADATA;
	private PreparedStatement SEC;
	private PreparedStatement DICT;
	private int write_no = 1;
	private final int COMMIT_SIZE = 100;
	private final ObjectMapper mapper = new ObjectMapper();
	private CryptoKey key;
	
	protected Dictionaries dictionaries = new Dictionaries();

	public SqliteADFWriter(CryptoKey key) {
		this.key = key;
	}

	@Override
	public void open(String temporaryDirectory) throws Exception {
		try {
			this.DB_LOCATION = temporaryDirectory + "/" + ADF_SQLITE_TEMP;
			connection = DriverManager.getConnection("jdbc:sqlite:" + DB_LOCATION);
			create();
			connection.setAutoCommit(false);
			this.P_DETECTIONS = connection.prepareStatement("INSERT INTO P_DETECTIONS VALUES(?, ?, ?, ?) ON CONFLICT DO UPDATE SET P=P+?, N=N+?");
			this.V_DETECTIONS = connection.prepareStatement("INSERT INTO V_DETECTIONS VALUES(?, ?, ?, ?, ?) ON CONFLICT DO UPDATE SET P=P+?, N=N+?");
			this.P_PROVIDER_DETECTIONS = connection.prepareStatement("INSERT INTO P_PROVIDER_DETECTIONS VALUES(?, ?, ?, ?, ?) ON CONFLICT DO UPDATE SET P=P+?, N=N+?");
			this.P_VOCAB = connection.prepareStatement("INSERT INTO P_VOCAB VALUES(?, ?, ?, ?) ON CONFLICT DO UPDATE SET N=N+?");
			this.V_VOCAB = connection.prepareStatement("INSERT INTO V_VOCAB VALUES(?, ?, ?, ?, ?) ON CONFLICT DO UPDATE SET N=N+?");
			this.P_PROVIDER_VOCAB = connection.prepareStatement("INSERT INTO P_PROVIDER_VOCAB VALUES(?, ?, ?, ?, ?) ON CONFLICT DO UPDATE SET N=N+?");
			this.V_EVENTS = connection.prepareStatement("INSERT INTO V_EVENTS VALUES(?, ?, ?, ?, ?, ?, ?) ON CONFLICT DO UPDATE SET N=N+?");
			this.METADATA = connection.prepareStatement("INSERT INTO METADATA VALUES(?, ?, ?, ?)");
			this.DICT = connection.prepareStatement("INSERT INTO DICTIONARY VALUES(?, ?)");
			this.SEC = connection.prepareStatement("INSERT INTO SEC VALUES(?, ?)");
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

	@Override
	public void exportAndClose(String location) throws Exception {
		connection.commit();
		SecretKeySpec secret = crypto.generateAESKey();
		// WRITE METADATA
		this.METADATA.setBytes(1, convertAndEncode(this.metadata, secret));
		this.METADATA.setBytes(2, convertAndEncode(this.getSummary(), secret));
		this.METADATA.setBytes(3, convertAndEncode(this.configurationPayload, secret));
		this.METADATA.setString(4, getVersion().name());
		this.METADATA.execute();
		// WRITE DICTIONARIES
		this.write_dictionary(Field.PROVIDER, secret);
		this.write_dictionary(Field.AGE_GROUP, secret);
		this.write_dictionary(Field.TABLE, secret);
		this.write_dictionary(Field.DETECTION, secret);
		this.write_dictionary(Field.VACCINE_CODE, secret);
		this.write_dictionary(Field.GENDER, secret);
		this.write_dictionary(Field.VACCINATION_YEAR, secret);
		this.write_dictionary(Field.EVENT, secret);
		this.write_dictionary(Field.CODE, secret);
		// WRITE SECURITY
		this.SEC.setBytes(1, key.getPublicKeyHash());
		this.SEC.setBytes(2, IOUtils.toByteArray(crypto.encodeRSA(new ByteArrayInputStream(secret.getEncoded()), key.getPublicKey())));
		this.SEC.execute();
		// COMPRESS
		connection.commit();
		connection.setAutoCommit(true);
		this.connection.createStatement().execute("VACUUM ");
		// ZIP AND COPY TO LOCATION
		zip(location);
		// TAG WITH ADF VERSION
		tag(location);
		// CLOSE
		this.close();
	}

	public void zip(String location) throws IOException {
		FileOutputStream fos = new FileOutputStream(location);
		ZipOutputStream zipOut = new ZipOutputStream(fos);

		File fileToZip = new File(this.DB_LOCATION);
		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry("ADF.data");
		zipOut.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}

		zipOut.close();
		fis.close();
		fos.close();
	}

	public void tag(String location) throws IOException {
		File zip = new File(location);
		File tagged = new File(location + "_tagged");
		FileChannel changed = new RandomAccessFile(tagged, "rw").getChannel();
		FileChannel original = new RandomAccessFile(zip, "r").getChannel();
		ByteBuffer buffer = ByteBuffer.wrap(getVersion().name().getBytes(StandardCharsets.UTF_8));
		changed.write(buffer);
		long size = original.size();
		long transferred = 0;
		do {
			transferred += original.transferTo(0, size, changed);
		} while (transferred < size);
		changed.close();
		original.close();
		Files.move(tagged.toPath(), zip.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

	private void write_dictionary(Field field, SecretKeySpec key) throws Exception {
		this.DICT.setString(1, field.name());
		this.DICT.setBytes(2, convertAndEncode(dictionaries.getValues(field), key));
		this.DICT.execute();
	}

	public <T> byte[] convertAndEncode(T value, SecretKeySpec key) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		mapper.writeValue(baos, value);
		return IOUtils.toByteArray(crypto.encodeAES(new ByteArrayInputStream(baos.toByteArray()), key.getEncoded()));
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
		this.P_DETECTIONS.setInt(1, dictionaries.getId(Field.AGE_GROUP, ageGroup));
		this.P_DETECTIONS.setInt(2, dictionaries.getId(Field.DETECTION, code));
		this.P_DETECTIONS.setInt(3, p);
		this.P_DETECTIONS.setInt(4, n);
		this.P_DETECTIONS.setInt(5, p);
		this.P_DETECTIONS.setInt(6, n);
		this.P_DETECTIONS.execute();
	}

	protected void write_p_vocab(String ageGroup, String table, String code, int nb) throws SQLException {
		this.P_VOCAB.setInt(1, dictionaries.getId(Field.AGE_GROUP, ageGroup));
		this.P_VOCAB.setInt(2, dictionaries.getId(Field.TABLE, table));
		this.P_VOCAB.setInt(3, dictionaries.getId(Field.CODE, code));
		this.P_VOCAB.setInt(4, nb);
		this.P_VOCAB.setInt(5, nb);
		this.P_VOCAB.execute();
	}

	protected void provider_detections(String provider, String ageGroup, String code, int p, int n, boolean patient) throws SQLException {
		PreparedStatement STAT = patient ? this.P_PROVIDER_DETECTIONS : this.V_DETECTIONS;
		STAT.setInt(1, dictionaries.getId(Field.PROVIDER, provider));
		STAT.setInt(2, dictionaries.getId(Field.AGE_GROUP, ageGroup));
		STAT.setInt(3, dictionaries.getId(Field.DETECTION, code));
		STAT.setInt(4, p);
		STAT.setInt(5, n);
		STAT.setInt(6, p);
		STAT.setInt(7, n);
		STAT.execute();
	}

	protected void provider_vocab(String provider, String ageGroup, String table, String code, int nb, boolean patient) throws SQLException {
		PreparedStatement STAT = patient ? this.P_PROVIDER_VOCAB : this.V_VOCAB;
		STAT.setInt(1, dictionaries.getId(Field.PROVIDER, provider));
		STAT.setInt(2, dictionaries.getId(Field.AGE_GROUP, ageGroup));
		STAT.setInt(3, dictionaries.getId(Field.TABLE, table));
		STAT.setInt(4, dictionaries.getId(Field.CODE, code));
		STAT.setInt(5, nb);
		STAT.setInt(6, nb);
		STAT.execute();
	}

	protected void write_v_events(String provider, String ageGroup, String year, String gender, String source, String code, int nb) throws SQLException {
		this.V_EVENTS.setInt(1, dictionaries.getId(Field.PROVIDER, provider));
		this.V_EVENTS.setInt(2, dictionaries.getId(Field.AGE_GROUP, ageGroup));
		this.V_EVENTS.setInt(3, dictionaries.getId(Field.VACCINATION_YEAR, year));
		this.V_EVENTS.setInt(4, dictionaries.getId(Field.GENDER, gender));
		this.V_EVENTS.setInt(5, dictionaries.getId(Field.EVENT, source));
		this.V_EVENTS.setInt(6, dictionaries.getId(Field.VACCINE_CODE, code));
		this.V_EVENTS.setInt(7, nb);
		this.V_EVENTS.setInt(8, nb);
		this.V_EVENTS.execute();
	}
}
