package gov.nist.healthcare.iz.darq.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.iz.darq.adf.service.ADFStoreUploadHandler;
import gov.nist.healthcare.iz.darq.adf.service.exception.InvalidFileFormat;
import gov.nist.healthcare.iz.darq.adf.utils.crypto.CryptoUtils;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;

@Service
public class ADFUploadHandler implements ADFStoreUploadHandler {
	@Autowired
	private ADFService adfService;
	@Autowired
	private CryptoUtils crypto;

	@Override
	public void handle(String name, String facility, InputStream stream, String ownerId, long size) throws InvalidFileFormat {
		try {
			byte[] content = IOUtils.toByteArray(stream);
			ADFile file = crypto.decryptFile(new ByteArrayInputStream(content));
			this.adfService.create(name, facility, ownerId, content, file, null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidFileFormat("Invalid ADF File Format");
		}
	}

}
