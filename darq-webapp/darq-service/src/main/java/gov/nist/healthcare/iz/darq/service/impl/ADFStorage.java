package gov.nist.healthcare.iz.darq.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

import com.google.common.base.Strings;
import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.repository.AccountRepository;
import gov.nist.healthcare.auth.service.AccountService;
import gov.nist.healthcare.iz.darq.model.UserUploadedFile;
import gov.nist.healthcare.iz.darq.service.FacilityService;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
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
public class ADFStorage implements ADFStore<UserUploadedFile> {

	@Autowired
	private ADFMetaDataRepository repo;
	@Autowired
	private CryptoUtils crypto;
	@Value("#{environment.DARQ_STORE}")
	private String PATH;
	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private FacilityService facilityService;
	
	@Override
	public String store(UserUploadedFile metadata) {
		repo.save(metadata);
		return metadata.getId();
	}

	@Override
	public boolean delete(String id, String owner) throws IOException {
		ADFMetaData md = this.repo.findOne(id);
		if(md != null && (md.getOwner().equals(owner) || this.accountService.isAdmin(owner))){
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
	public UserUploadedFile getAccessible(String id, String owner) throws NotFoundException {
		Account user = this.accountRepository.findByUsername(owner);
		UserUploadedFile md = this.repo.findOne(id);

		if(md == null) {
			throw new NotFoundException("ADF "+id+" Not Found");
		}

		boolean hasFacility = !Strings.isNullOrEmpty(md.getFacilityId());

		if((md.getOwner().equals(owner) || this.accountService.isAdmin(owner) || hasFacility) && this.facilityService.canSeeFacility(md.getFacilityId(), user)){
			return md;
		} else {
			throw new NotFoundException("ADF "+id+" Not Found");
		}
	}

	@Override
	public ADFile getFile(String id, String owner) throws Exception {
		UserUploadedFile md = this.getAccessible(id, owner);
		if(md != null){
			FileInputStream fis = new FileInputStream(Paths.get(PATH+"/"+md.getPath()+"/adf.data").toFile());
			byte[] content = IOUtils.toByteArray(fis);
			return crypto.decrypt(content);
		}
		return null;
	}
	

}
