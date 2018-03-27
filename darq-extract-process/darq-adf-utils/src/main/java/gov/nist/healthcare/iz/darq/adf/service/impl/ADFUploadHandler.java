package gov.nist.healthcare.iz.darq.adf.service.impl;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	@Override
	public void handle(String name, InputStream stream, String owner, long size) throws InvalidFileFormat {
		
		try {
			byte[] content = IOUtils.toByteArray(stream);
			ADFile file = crypto.decrypt(content);
			String uid = UUID.randomUUID().toString();
			Path dir = Files.createDirectory(Paths.get("/Users/hnt5/adf_storage/"+uid));
			if (dir.toFile().exists()) {
				ADFMetaData metadata = new ADFMetaData(name, uid, owner, file.getAnalysisDate(), new Date(), file.getConfiguration(), file.getSummary(), humanReadableByteCount(size, true));
				storage.store(metadata);
				Files.write(Paths.get(dir.toString(), "adf.data"), content);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidFileFormat();
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
