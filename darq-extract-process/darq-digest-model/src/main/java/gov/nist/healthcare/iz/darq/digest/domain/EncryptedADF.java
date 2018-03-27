package gov.nist.healthcare.iz.darq.digest.domain;

public class EncryptedADF {
	public byte[] key;
	public String hash;
	public byte[] content;
	
	
	public EncryptedADF() {
		super();
	}

	public EncryptedADF(byte[] key, byte[] content) {
		super();
		this.key = key;
		this.content = content;
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

}
