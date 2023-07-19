package gov.nist.healthcare.iz.darq.adf.module.sqlite;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import gov.nist.healthcare.iz.darq.adf.model.Metadata;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.adf.module.sqlite.model.Dictionaries;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.domain.Summary;
import org.apache.commons.io.IOUtils;

import javax.crypto.CipherInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SqliteADFReader implements ADFReader {
	private Connection connection;
	private final String location;
	private final String temporaryInflateDirectory;
	private String inflatedFileLocation;
	private final ObjectMapper mapper = new ObjectMapper();
	private Metadata metadata;
	private Summary summary;
	private ConfigurationPayload configurationPayload;
	private PreparedStatement KEY;
	private PreparedStatement DICTIONARY;
	private PreparedStatement METADATA;
	private byte[] keyHash;
	private boolean open = false;
	private boolean ready = false;
	private Dictionaries dictionaries = new Dictionaries();

	public SqliteADFReader(String location, String temporaryInflateDirectory) {
		this.location = location;
		this.temporaryInflateDirectory = temporaryInflateDirectory;
	}

	@Override
	public void open(CryptoKey key) throws Exception {
		Path uncompressed = unzip();
		this.inflatedFileLocation = uncompressed.toAbsolutePath().toString();
		connection = DriverManager.getConnection("jdbc:sqlite:" + this.inflatedFileLocation);
		this.METADATA = connection.prepareStatement("SELECT * FROM METADATA");
		this.KEY = connection.prepareStatement("SELECT * FROM SEC");
		this.DICTIONARY = connection.prepareStatement("SELECT DICT FROM DICTIONARY WHERE ID = ?");
		this.
		open = true;
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

	private Path unzip () throws Exception {
		String filename = UUID.randomUUID().toString();
		Path inflateFile = Paths.get(temporaryInflateDirectory, filename);
		InputStream fis = Files.newInputStream(Paths.get(location));
		// Skip Magic Value (ADF Version)
		long skipped = fis.skip(getVersion().name().length());
		byte[] buffer = new byte[1024];
		ZipInputStream zis = new ZipInputStream(fis);


		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {
			if(zipEntry.getName().equals("ADF.data")) {
				FileOutputStream fos = new FileOutputStream(inflateFile.toFile());
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				return inflateFile;
			}
			zipEntry = zis.getNextEntry();
		}

		zis.closeEntry();
		zis.close();
		throw new Exception("ADF.data not found in archive");
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
	}

	private void readDictionary(Field field, byte[] secretBytes) throws Exception {
		TypeReference<HashMap<String, Integer>> typeRef  = new TypeReference<HashMap<String, Integer>>() {};
		this.DICTIONARY.setString(1, field.name());
		ResultSet metaResultSet = this.DICTIONARY.executeQuery();
		while (metaResultSet.next()) {
			this.dictionaries.put(field, decodeAndConvert(metaResultSet.getBinaryStream(1), secretBytes, typeRef));
		}
	}

	private void readMetadataTable(byte[] secretBytes) throws Exception {
		ResultSet metaResultSet = this.METADATA.executeQuery();
		while (metaResultSet.next()) {
			this.metadata = decodeAndConvert(metaResultSet.getBinaryStream("INFO"), secretBytes, Metadata.class);
			this.configurationPayload = decodeAndConvert(metaResultSet.getBinaryStream("CONFIGURATION"), secretBytes, ConfigurationPayload.class);
			this.summary = decodeAndConvert(metaResultSet.getBinaryStream("SUMMARY"), secretBytes, Summary.class);
		}
	}

	private byte[] readKey(CryptoKey key) throws Exception{
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
		this.connection.close();
		Files.deleteIfExists(Paths.get(inflatedFileLocation));
		open = false;
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
