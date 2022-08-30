package gov.nist.healthcare.iz.darq.service.impl;

import java.io.*;
import java.nio.file.Paths;

import gov.nist.healthcare.iz.darq.digest.domain.*;
import gov.nist.healthcare.iz.darq.model.UserUploadedFile;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.iz.darq.adf.service.ADFStore;
import gov.nist.healthcare.iz.darq.adf.utils.crypto.CryptoUtils;
import gov.nist.healthcare.iz.darq.repository.ADFMetaDataRepository;

@Service
public class ADFStorage implements ADFStore<UserUploadedFile> {
	public static final String ADF_FILENAME = "adf.data";

	@Autowired
	private ADFMetaDataRepository repo;
	@Autowired
	private CryptoUtils crypto;
	@Value("#{environment.QDAR_STORE}")
	private String PATH;


	@Override
	public String store(UserUploadedFile metadata) {
		repo.save(metadata);
		return metadata.getId();
	}

	@Override
	public boolean delete(String id) throws IOException {
		ADFMetaData md = this.repo.findOne(id);
		if(md != null){
			FileUtils.deleteDirectory(Paths.get(PATH+"/"+md.getPath()).toFile());
			repo.delete(id);
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public UserUploadedFile get(String id) {
		return repo.findOne(id);
	}

	@Override
	public ADFile getFile(String id) throws Exception {
		UserUploadedFile md = this.get(id);
		if(md != null){
			return crypto.decryptFile(this.getFileInputStream(md.getPath()));
		}
		return null;
	}

	@Override
	public InputStream getFileInputStream(String pathId) throws Exception {
		return new FileInputStream(Paths.get(PATH + "/" + pathId + "/" + ADF_FILENAME).toFile());
	}

}
