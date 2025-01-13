package gov.nist.healthcare.iz.darq.adf.module.sqlite;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import gov.nist.healthcare.iz.darq.adf.model.Metadata;
import gov.nist.healthcare.iz.darq.adf.module.sqlite.model.Dictionaries;
import gov.nist.healthcare.iz.darq.adf.utils.crypto.ADFCryptoUtil;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.domain.Summary;
import org.apache.commons.io.IOUtils;

import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Objects;

public class SqliteADFWriterDAO {

	private Connection connection;
	private ObjectMapper mapper = new ObjectMapper();
	protected final ADFCryptoUtil crypto = new ADFCryptoUtil();
	private PreparedStatement P_DETECTIONS;
	private PreparedStatement P_VOCAB;
	private PreparedStatement P_PROVIDER_DETECTIONS;
	private PreparedStatement P_PROVIDER_VOCAB;
	private PreparedStatement P_MATCH_SIGNATURE;
	private PreparedStatement V_DETECTIONS;
	private PreparedStatement V_VOCAB;
	private PreparedStatement V_EVENTS;
	private PreparedStatement METADATA;
	private PreparedStatement SEC;
	private PreparedStatement DICT;

	public SqliteADFWriterDAO(Connection connection) throws SQLException {
		this.connection = connection;
		initialize();
		this.P_DETECTIONS = connection.prepareStatement("INSERT INTO P_DETECTIONS VALUES(?, ?, ?, ?) ON CONFLICT DO UPDATE SET P=P+?, N=N+?");
		this.V_DETECTIONS = connection.prepareStatement("INSERT INTO V_DETECTIONS VALUES(?, ?, ?, ?, ?) ON CONFLICT DO UPDATE SET P=P+?, N=N+?");
		this.P_PROVIDER_DETECTIONS = connection.prepareStatement("INSERT INTO P_PROVIDER_DETECTIONS VALUES(?, ?, ?, ?, ?) ON CONFLICT DO UPDATE SET P=P+?, N=N+?");
		this.P_VOCAB = connection.prepareStatement("INSERT INTO P_VOCAB VALUES(?, ?, ?, ?) ON CONFLICT DO UPDATE SET N=N+?");
		this.V_VOCAB = connection.prepareStatement("INSERT INTO V_VOCAB VALUES(?, ?, ?, ?, ?) ON CONFLICT DO UPDATE SET N=N+?");
		this.P_MATCH_SIGNATURE = connection.prepareStatement("INSERT INTO P_MATCH_SIGNATURE VALUES (?, ?) ON CONFLICT DO UPDATE SET N=N+?");
		this.P_PROVIDER_VOCAB = connection.prepareStatement("INSERT INTO P_PROVIDER_VOCAB VALUES(?, ?, ?, ?, ?) ON CONFLICT DO UPDATE SET N=N+?");
		this.V_EVENTS = connection.prepareStatement("INSERT INTO V_EVENTS VALUES(?, ?, ?, ?, ?, ?, ?) ON CONFLICT DO UPDATE SET N=N+?");
		this.METADATA = connection.prepareStatement("INSERT INTO METADATA VALUES(?, ?, ?, ?)");
		this.DICT = connection.prepareStatement("INSERT INTO DICTIONARY VALUES(?, ?)");
		this.SEC = connection.prepareStatement("INSERT INTO SEC VALUES(?, ?)");
	}

	void initialize() throws SQLException {
		Statement statement = connection.createStatement();
		statement.executeQuery("PRAGMA journal_mode=OFF");
		statement.execute("PRAGMA synchronous=OFF ");
		new BufferedReader(new InputStreamReader(Objects.requireNonNull(SqliteADFWriter.class.getResourceAsStream("/adf_schema.sql")),
		                                         StandardCharsets.UTF_8)).lines().forEach((line) -> {
			try {
				statement.execute(line);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		});
	}

	public void write_p_detections(int ageGroup, int code, int p, int n) throws SQLException {
		this.P_DETECTIONS.setInt(1, ageGroup);
		this.P_DETECTIONS.setInt(2, code);
		this.P_DETECTIONS.setInt(3, p);
		this.P_DETECTIONS.setInt(4, n);
		this.P_DETECTIONS.setInt(5, p);
		this.P_DETECTIONS.setInt(6, n);
		this.P_DETECTIONS.execute();
	}

	public void write_p_vocab(int ageGroup, int table, int code, int nb) throws SQLException {
		this.P_VOCAB.setInt(1, ageGroup);
		this.P_VOCAB.setInt(2, table);
		this.P_VOCAB.setInt(3, code);
		this.P_VOCAB.setInt(4, nb);
		this.P_VOCAB.setInt(5, nb);
		this.P_VOCAB.execute();
	}

	public void write_provider_detections(int provider, int ageGroup, int code, int p, int n, boolean patient) throws SQLException {
		PreparedStatement STAT = patient ? this.P_PROVIDER_DETECTIONS : this.V_DETECTIONS;
		STAT.setInt(1, provider);
		STAT.setInt(2, ageGroup);
		STAT.setInt(3, code);
		STAT.setInt(4, p);
		STAT.setInt(5, n);
		STAT.setInt(6, p);
		STAT.setInt(7, n);
		STAT.execute();
	}

	public void write_provider_vocab(int provider, int ageGroup, int table, int code, int nb, boolean patient) throws SQLException {
		PreparedStatement STAT = patient ? this.P_PROVIDER_VOCAB : this.V_VOCAB;
		STAT.setInt(1, provider);
		STAT.setInt(2, ageGroup);
		STAT.setInt(3, table);
		STAT.setInt(4, code);
		STAT.setInt(5, nb);
		STAT.setInt(6, nb);
		STAT.execute();
	}

	public void write_v_events(int provider, int ageGroup, int year, int gender, int source, int code, int nb) throws SQLException {
		this.V_EVENTS.setInt(1, provider);
		this.V_EVENTS.setInt(2, ageGroup);
		this.V_EVENTS.setInt(3, year);
		this.V_EVENTS.setInt(4, gender);
		this.V_EVENTS.setInt(5, source);
		this.V_EVENTS.setInt(6, code);
		this.V_EVENTS.setInt(7, nb);
		this.V_EVENTS.setInt(8, nb);
		this.V_EVENTS.execute();
	}

	public void write_dictionary(Field field, Map<String, Integer> values, SecretKeySpec key) throws Exception {
		this.DICT.setString(1, field.name());
		this.DICT.setBytes(2, convertAndEncode(values, key));
		this.DICT.execute();
	}

	public void write_metadata(Metadata metadata, Summary summary, ConfigurationPayload payload, ADFVersion version, SecretKeySpec secret) throws Exception {
		this.METADATA.setBytes(1, convertAndEncode(metadata, secret));
		this.METADATA.setBytes(2, convertAndEncode(summary, secret));
		this.METADATA.setBytes(3, convertAndEncode(payload, secret));
		this.METADATA.setString(4, version.name());
		this.METADATA.execute();
	}

	public void write_sec(CryptoKey key, SecretKeySpec secret) throws Exception {
		this.SEC.setBytes(1, key.getPublicKeyHash());
		this.SEC.setBytes(2, IOUtils.toByteArray(crypto.encodeRSA(new ByteArrayInputStream(secret.getEncoded()), key.getPublicKey())));
		this.SEC.execute();
	}

	public void write_dictionaries(Dictionaries dictionaries, SecretKeySpec secret) throws Exception {
		this.write_dictionary(Field.PROVIDER, dictionaries.getValues(Field.PROVIDER), secret);
		this.write_dictionary(Field.AGE_GROUP, dictionaries.getValues(Field.AGE_GROUP), secret);
		this.write_dictionary(Field.TABLE, dictionaries.getValues(Field.TABLE), secret);
		this.write_dictionary(Field.DETECTION, dictionaries.getValues(Field.DETECTION), secret);
		this.write_dictionary(Field.VACCINE_CODE, dictionaries.getValues(Field.VACCINE_CODE), secret);
		this.write_dictionary(Field.GENDER, dictionaries.getValues(Field.GENDER), secret);
		this.write_dictionary(Field.VACCINATION_YEAR, dictionaries.getValues(Field.VACCINATION_YEAR), secret);
		this.write_dictionary(Field.EVENT, dictionaries.getValues(Field.EVENT), secret);
		this.write_dictionary(Field.CODE, dictionaries.getValues(Field.CODE), secret);
		this.write_dictionary(Field.MATCH_SIGNATURE, dictionaries.getValues(Field.MATCH_SIGNATURE), secret);
	}

	public void write_match_signature(int signature, int n) throws SQLException {
		this.P_MATCH_SIGNATURE.setInt(1, signature);
		this.P_MATCH_SIGNATURE.setInt(2, n);
		this.P_MATCH_SIGNATURE.setInt(3, n);
		this.P_MATCH_SIGNATURE.execute();
	}

	private <T> byte[] convertAndEncode(T value, SecretKeySpec key) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		mapper.writeValue(baos, value);
		return IOUtils.toByteArray(crypto.encodeAES(new ByteArrayInputStream(baos.toByteArray()), key.getEncoded()));
	}

	public Connection getConnection() {
		return connection;
	}
}
