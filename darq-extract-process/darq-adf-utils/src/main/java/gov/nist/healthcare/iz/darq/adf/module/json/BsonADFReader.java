package gov.nist.healthcare.iz.darq.adf.module.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.undercouch.bson4jackson.BsonFactory;
import de.undercouch.bson4jackson.BsonGenerator;
import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import gov.nist.healthcare.iz.darq.adf.model.Metadata;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.adf.module.json.model.ADFile;
import gov.nist.healthcare.iz.darq.adf.module.json.model.EncryptedADF;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Summary;
import org.apache.commons.io.IOUtils;

import javax.crypto.CipherInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class BsonADFReader implements ADFReader {
	private final Path location;
	private static final ObjectMapper mapper = new ObjectMapper(new BsonFactory().enable(BsonGenerator.Feature.ENABLE_STREAMING)).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	EncryptedADF encryptedADF;
	byte[] keyHash;
	private ADFile file;
	boolean open = false;
	boolean ready = false;

	public BsonADFReader(Path location) {
		this.location = location;
	}

	@Override
	public void open(CryptoKey key) throws Exception {
		FileInputStream fileInputStream = new FileInputStream(location.toFile());
		encryptedADF = mapper.readValue(fileInputStream, EncryptedADF.class);
		open = true;
	}

	@Override
	public void read(CryptoKey key) throws Exception {
		if(!isOpen()) {
			open(key);
		}
		if(!ready) {
			// -- Decode AES Key
			CipherInputStream keyCipherInputStream = crypto.decodeRSA(new ByteArrayInputStream(encryptedADF.getKey()), key.getPrivateKey());
			byte[] decodedAESKey = IOUtils.toByteArray(keyCipherInputStream);

			// -- Decode ADF Content
			CipherInputStream contentCipherInputStream = crypto.decodeAES(encryptedADF.content, decodedAESKey);
			this.file = mapper.readValue(contentCipherInputStream, ADFile.class);
			keyHash = encryptedADF.getKeyHash();
			this.encryptedADF = null;
			ready = true;
		}
	}

	@Override
	public void close() throws Exception {
		this.file = null;
		this.encryptedADF = null;
		open = false;
	}

	@Override
	public ConfigurationPayload getConfigurationPayload() {
		if(file == null) return null;
		return file.getConfiguration();
	}

	@Override
	public Metadata getMetadata() {
		if(file == null) return null;
		return new Metadata(
				file.getVersion(),
				file.getBuild(),
				file.getMqeVersion(),
				file.getTotalAnalysisTime(),
				file.getAnalysisDate(),
				file.getInactiveDetections()
		);
	}

	@Override
	public Summary getSummary() {
		if(file == null) return null;
		return file.getSummary();
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
		return ADFVersion.ADFBSON000001;
	}

	@Override
	public boolean checkADFKey(CryptoKey key) throws Exception {
		if(isReady()) return Arrays.equals(this.keyHash, key.getPublicKeyHash());
		else {
			if(!isOpen()) {
				open(key);
			}
			if(encryptedADF != null) {
				return Arrays.equals(encryptedADF.keyHash, key.getPublicKeyHash());
			} else {
				return false;
			}
		}
	}

	@Override
	public Path getADFLocation() {
		return location;
	}

	@Override
	public long getFileSize() throws IOException {
		return Files.size(getADFLocation());
	}

	public ADFile getADF() {
		return this.file;
	}
}
