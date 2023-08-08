package gov.nist.healthcare.iz.darq.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.module.ADFManager;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.iz.darq.adf.service.ADFStoreUploadHandler;
import gov.nist.healthcare.iz.darq.adf.service.exception.InvalidFileFormat;

@Service
public class ADFUploadHandler implements ADFStoreUploadHandler {
	@Autowired
	private ADFService adfService;
	@Autowired
	@Qualifier("ADF_KEYS")
	private CryptoKey keys;
	@Autowired
	private ADFManager manager;
	@Autowired
	ADFTemporaryDirectoryProviderService temporaryDirectoryProviderService;

	@Override
	public void handle(String name, String facility, InputStream stream, String ownerId, long size) throws InvalidFileFormat {
		// Write stream to temporary directory
		Path temporary = temporaryDirectoryProviderService.getADFTemporaryDirectoryPath();
		Path temporaryAdf = Paths.get(temporary.toAbsolutePath().toString(), UUID.randomUUID() + "_uploaded_"+ (new Date()).getTime() +".data");

		try {
			FileUtils.copyInputStreamToFile(stream, temporaryAdf.toFile());
			try(ADFReader file = manager.getADFReader(temporaryAdf.toAbsolutePath().toString())) {
				file.read(keys);
				this.adfService.create(name, facility, ownerId, file, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidFileFormat("Invalid ADF File Format");
		} finally {
			try {
				Files.deleteIfExists(temporaryAdf);
			} catch (IOException ignored) {}
		}
	}

}
