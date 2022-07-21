package gov.nist.healthcare.iz.darq.digest.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import gov.nist.healthcare.iz.darq.digest.domain.mapper.InputStreamDeserializer;
import gov.nist.healthcare.iz.darq.digest.domain.mapper.InputStreamSerializer;

import java.io.InputStream;

public class EncryptedADF {
	public byte[] key;
	@JsonSerialize(using = InputStreamSerializer.class)
	@JsonDeserialize(using = InputStreamDeserializer.class)
	public InputStream content;
	public byte[] keyHash;
	
	public EncryptedADF() {
		super();
	}

	public EncryptedADF(byte[] key, InputStream content, byte[] keyHash) {
		super();
		this.key = key;
		this.content = content;
		this.keyHash = keyHash;
	}
	
	public byte[] getKey() {
		return key;
	}
	public void setKey(byte[] key) {
		this.key = key;
	}
	public InputStream getContent() {
		return content;
	}
	public void setContent(InputStream content) {
		this.content = content;
	}
	public byte[] getKeyHash() {
		return keyHash;
	}
	public void setKeyHash(byte[] keyHash) {
		this.keyHash = keyHash;
	}
}
