package gov.nist.healthcare.iz.darq.service.impl;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

import gov.nist.healthcare.iz.darq.model.UserUploadedFile;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.iz.darq.adf.service.ADFStore;
import gov.nist.healthcare.iz.darq.adf.service.ADFStoreUploadHandler;
import gov.nist.healthcare.iz.darq.adf.service.exception.InvalidFileFormat;
import gov.nist.healthcare.iz.darq.adf.utils.crypto.CryptoUtils;
import gov.nist.healthcare.iz.darq.digest.domain.ADFMetaData;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;

@Service
public class ADFUploadHandler implements ADFStoreUploadHandler {

	@Autowired
	private ADFStore storage;
	@Autowired
	private CryptoUtils crypto;

	@Value("#{environment.DARQ_STORE}")
	private String PATH;
	
	@Override
	public void handle(String name, String facility, InputStream stream, String owner, long size) throws InvalidFileFormat {
		
		try {
			byte[] content = IOUtils.toByteArray(stream);
			ADFile file = crypto.decrypt(content);
			String uid = UUID.randomUUID().toString();
			Path dir = Files.createDirectory(Paths.get(PATH+"/"+uid));
			if (dir.toFile().exists()) {
				UserUploadedFile metadata = new UserUploadedFile(
						name,
						uid,
						owner,
						file.getAnalysisDate(),
						new Date(),
						file.getConfiguration(),
						"",
						file.getSummary(),
						humanReadableByteCount(size, true),
						file.getVersion(),
						file.getBuild(),
						file.getMqeVersion(),
						file.getInactiveDetections(),
						facility
				);
				storage.store(metadata);
				Files.write(Paths.get(dir.toString(), "/adf.data"), content);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidFileFormat("Invalid ADF File Format");
		}
	}
	
	public String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

}
