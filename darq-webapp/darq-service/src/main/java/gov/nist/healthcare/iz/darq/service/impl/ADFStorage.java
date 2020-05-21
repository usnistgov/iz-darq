package gov.nist.healthcare.iz.darq.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

import gov.nist.healthcare.iz.darq.model.UserUploadedFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.iz.darq.adf.service.ADFStore;
import gov.nist.healthcare.iz.darq.adf.utils.crypto.CryptoUtils;
import gov.nist.healthcare.iz.darq.digest.domain.ADFMetaData;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;
import gov.nist.healthcare.iz.darq.repository.ADFMetaDataRepository;

@Service
//@PropertySource("classpath:/configuration.properties")
public class ADFStorage implements ADFStore<UserUploadedFile> {

	@Autowired
	private ADFMetaDataRepository repo;
	@Autowired
	private CryptoUtils crypto;
	@Value("#{environment.DARQ_STORE}")
	private String PATH;
	
	@Override
	public String store(UserUploadedFile metadata) {
		repo.save(metadata);
		return metadata.getId();
	}

	@Override
	public boolean delete(String id, String owner) throws IOException {
		ADFMetaData md = get(id, owner);
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
	public UserUploadedFile get(String id, String owner) {
		return repo.findByIdAndOwner(id, owner);
	}

	@Override
	public ADFile getFile(String id, String owner) throws Exception {
		ADFMetaData md = get(id, owner);
		if(md != null){
			FileInputStream fis = new FileInputStream(Paths.get(PATH+"/"+md.getPath()+"/adf.data").toFile());
			byte[] content = IOUtils.toByteArray(fis);
			ADFile file = crypto.decrypt(content);
			return file;
		}
		return null;
	}
	

}
