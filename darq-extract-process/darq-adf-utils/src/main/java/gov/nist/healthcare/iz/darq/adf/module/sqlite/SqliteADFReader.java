package gov.nist.healthcare.iz.darq.adf.module.sqlite;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import gov.nist.healthcare.iz.darq.adf.model.Metadata;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.adf.module.archive.ADFArchiveManager;
import gov.nist.healthcare.iz.darq.adf.module.sqlite.model.Dictionaries;
import gov.nist.healthcare.iz.darq.digest.domain.AnalysisType;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.domain.Summary;
import org.apache.commons.io.IOUtils;

import javax.crypto.CipherInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

public class SqliteADFReader implements ADFReader {
	protected Connection connection;
	protected final String location;
	protected final String temporaryInflateDirectory;
	protected String inflatedFileLocation;
	protected final ObjectMapper mapper = new ObjectMapper();
	protected Metadata metadata;
	protected Summary summary;
	protected ConfigurationPayload configurationPayload;
	protected PreparedStatement KEY;
	protected PreparedStatement DICTIONARY;
	protected PreparedStatement METADATA;
	protected PreparedStatement CHECK_TABLE_EXISTS;
	protected byte[] keyHash;
	protected boolean open = false;
	protected boolean ready = false;
	protected final Dictionaries dictionaries = new Dictionaries();
	protected Map<AnalysisType, Boolean> analysisTypeSupport = new HashMap<>();

	public SqliteADFReader(String location, String temporaryInflateDirectory) {
		this.location = location;
		this.temporaryInflateDirectory = temporaryInflateDirectory;
	}

	public void inflate() throws Exception {
		String filename = UUID.randomUUID().toString();
		Path uncompressed = Paths.get(temporaryInflateDirectory, filename);
		ADFArchiveManager.getInstance().extract(location, uncompressed.toAbsolutePath().toString());
		this.inflatedFileLocation = uncompressed.toAbsolutePath().toString();
	}

	@Override
	public void open(CryptoKey key) throws Exception {
		inflate();
		Properties config = new Properties();
		config.setProperty("open_mode", "1");
		connection = DriverManager.getConnection("jdbc:sqlite:" + this.inflatedFileLocation, config);
		this.METADATA = connection.prepareStatement("SELECT * FROM METADATA");
		this.KEY = connection.prepareStatement("SELECT * FROM SEC");
		this.DICTIONARY = connection.prepareStatement("SELECT DICT FROM DICTIONARY WHERE ID = ?");
		this.CHECK_TABLE_EXISTS = connection.prepareStatement("SELECT name FROM sqlite_master WHERE type='table' AND name= ?");
		this.loadAnalysisTypeSupportMap();
		this.open = true;
	}

	@Override
	public void read(CryptoKey key) throws Exception {
		if(!isOpen()) {
			open(key);
		}
		if(!ready) {
			byte[] secret = readKey(key);
			readMetadataTable(secret);
			readDictionaries(secret);
			ready = true;
		}
	}

	private void readDictionaries(byte[] secret) throws Exception {
		readDictionary(Field.PROVIDER, secret);
		readDictionary(Field.AGE_GROUP, secret);
		readDictionary(Field.TABLE, secret);
		readDictionary(Field.DETECTION, secret);
		readDictionary(Field.VACCINE_CODE, secret);
		readDictionary(Field.GENDER, secret);
		readDictionary(Field.VACCINATION_YEAR, secret);
		readDictionary(Field.EVENT, secret);
		readDictionary(Field.CODE, secret);
		readDictionary(Field.MATCH_SIGNATURE, secret);
	}

	protected void readDictionary(Field field, byte[] secretBytes) throws Exception {
		TypeReference<HashMap<String, Integer>> typeRef  = new TypeReference<HashMap<String, Integer>>() {};
		this.DICTIONARY.setString(1, field.name());
		ResultSet metaResultSet = this.DICTIONARY.executeQuery();
		while (metaResultSet.next()) {
			this.dictionaries.put(field, decodeAndConvert(metaResultSet.getBinaryStream(1), secretBytes, typeRef));
		}
	}

	protected void readMetadataTable(byte[] secretBytes) throws Exception {
		ResultSet metaResultSet = this.METADATA.executeQuery();
		while (metaResultSet.next()) {
			this.metadata = decodeAndConvert(metaResultSet.getBinaryStream("INFO"), secretBytes, Metadata.class);
			this.configurationPayload = decodeAndConvert(metaResultSet.getBinaryStream("CONFIGURATION"), secretBytes, ConfigurationPayload.class);
			this.summary = decodeAndConvert(metaResultSet.getBinaryStream("SUMMARY"), secretBytes, Summary.class);
		}
	}

	protected byte[] readKey(CryptoKey key) throws Exception{
		ResultSet keyResultSet = this.KEY.executeQuery();
		InputStream secret = null;
		while (keyResultSet.next()) {
			secret = keyResultSet.getBinaryStream("_KEY");
			this.keyHash = IOUtils.toByteArray(keyResultSet.getBinaryStream("KEY_HASH"));
		}
		return IOUtils.toByteArray(crypto.decodeRSA(secret, key.getPrivateKey()));
	}

	private <T> T decodeAndConvert(InputStream value, byte[] key, Class<T> to) throws Exception {
		CipherInputStream decoded = crypto.decodeAES(value, key);
		return mapper.readValue(decoded, to);
	}

	private <T> T decodeAndConvert(InputStream value, byte[] key, TypeReference<T> to) throws Exception {
		CipherInputStream decoded = crypto.decodeAES(value, key);
		return mapper.readValue(decoded, to);
	}

	@Override
	public void close() throws Exception {
		try {
			if(connection != null) {
				this.connection.close();
			}
		} finally {
			if(inflatedFileLocation != null) {
				Files.deleteIfExists(Paths.get(inflatedFileLocation));
			}
			open = false;
		}
	}

	@Override
	public ConfigurationPayload getConfigurationPayload() {
		return configurationPayload;
	}

	@Override
	public Metadata getMetadata() {
		return metadata;
	}

	@Override
	public Summary getSummary() {
		return summary;
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public boolean isReady() {
		return ready;
	}

	@Override
	public ADFVersion getVersion() {
		return ADFVersion.ADFSQLITE0001;
	}

	@Override
	public long getFileSize() throws IOException {
		return Files.size(getADFLocation());
	}

	@Override
	public boolean checkADFKey(CryptoKey key) throws Exception {
		if(isReady()) return Arrays.equals(this.keyHash, key.getPublicKeyHash());
		else {
			if(!isOpen()) {
				open(key);
			}
			ResultSet keyResultSet = this.KEY.executeQuery();
			while (keyResultSet.next()) {
				byte[] keyHash = IOUtils.toByteArray(keyResultSet.getBinaryStream("KEY_HASH"));
				return Arrays.equals(keyHash, key.getPublicKeyHash());
			}
			return false;
		}
	}

	private boolean hasTable(String tableName) throws Exception {
		this.CHECK_TABLE_EXISTS.setString(1, tableName);
		ResultSet resultSet = this.CHECK_TABLE_EXISTS.executeQuery();
		return resultSet.next();
	}

	public boolean supportsAnalysisType(AnalysisType type) {
		return this.analysisTypeSupport.containsKey(type) && this.analysisTypeSupport.get(type);
	}

	public String getTableNameForAnalysis(AnalysisType type) {
		switch (type) {
			case V:
				return "V_EVENTS";
			case VD:
				return "V_DETECTIONS";
			case VT:
				return "V_VOCAB";
			case PD:
				return "P_DETECTIONS";
			case PT:
				return "P_VOCAB";
			case PD_RG:
				return "P_PROVIDER_DETECTIONS";
			case PT_RG:
				return "P_PROVIDER_VOCAB";
			case PM:
				return "P_MATCH_SIGNATURE";
			default:
				return type.name();
		}
	}

	private void loadAnalysisTypeSupportMap() throws Exception {
		this.analysisTypeSupport = new HashMap<>();
		for(AnalysisType type : AnalysisType.values()) {
			this.analysisTypeSupport.put(type, this.hasTable(getTableNameForAnalysis(type)));
		}
	}

	@Override
	public Path getADFLocation() {
		return Paths.get(location);
	}

	public Connection getConnection() {
		return this.connection;
	}

	public Dictionaries getDictionaries() {
		return this.dictionaries;
	}
}
