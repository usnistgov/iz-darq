package gov.nist.healthcare.iz.darq.digest.domain;

public class EncryptedADF {
	public byte[] key;
	public byte[] content;
	public byte[] keyHash;
	
	public EncryptedADF() {
		super();
	}

	public EncryptedADF(byte[] key, byte[] content, byte[] keyHash) {
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
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	public byte[] getKeyHash() {
		return keyHash;
	}
	public void setKeyHash(byte[] keyHash) {
		this.keyHash = keyHash;
	}
}
