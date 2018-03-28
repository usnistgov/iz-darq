package gov.nist.healthcare.iz.darq.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.iz.darq.adf.service.ADFStore;
import gov.nist.healthcare.iz.darq.adf.utils.crypto.CryptoUtils;
import gov.nist.healthcare.iz.darq.digest.domain.ADFMetaData;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;
import gov.nist.healthcare.iz.darq.repository.ADFMetaDataRepository;

@Service
public class ADFStorage implements ADFStore {

	@Autowired
	private ADFMetaDataRepository repo;
	@Autowired
	private CryptoUtils crypto;
	
	@Override
	public String store(ADFMetaData metadata) {
		repo.save(metadata);
		return metadata.getId();
	}

	@Override
	public boolean delete(String id, String owner) throws IOException {
		ADFMetaData md = get(id, owner);
		if(md != null){
			FileUtils.deleteDirectory(Paths.get("~/adf_storage/"+md.getPath()).toFile());
			repo.delete(id);
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public ADFMetaData get(String id, String owner) {
		return repo.findByIdAndOwner(id, owner);
	}

	@Override
	public ADFile getFile(String id, String owner) throws Exception {
		ADFMetaData md = get(id, owner);
		if(md != null){
			FileInputStream fis = new FileInputStream(Paths.get("/Users/hnt5/adf_storage/"+md.getPath()+"/adf.data").toFile());
			byte[] content = IOUtils.toByteArray(fis);
			ADFile file = crypto.decrypt(content);
			return file;
		}
		return null;
	}
	

}
