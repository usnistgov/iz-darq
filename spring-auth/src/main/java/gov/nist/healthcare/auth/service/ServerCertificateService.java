package gov.nist.healthcare.auth.service;

public interface ServerCertificateService {
	
	public byte[] encrypt(byte[] payload);
	public byte[] decrypt(byte[] payload);

}
