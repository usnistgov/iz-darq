package gov.nist.healthcare.iz.darq.repository;

import java.util.List;

import gov.nist.healthcare.iz.darq.model.UserUploadedFile;
import org.springframework.data.mongodb.repository.MongoRepository;

import gov.nist.healthcare.iz.darq.digest.domain.ADFMetaData;

public interface ADFMetaDataRepository extends MongoRepository<UserUploadedFile, String>{

	List<UserUploadedFile> findByOwner(String owner);
	List<UserUploadedFile> findByOwnerAndFacilityId(String owner, String facility);
	List<UserUploadedFile> findByFacilityId(String facility);
	UserUploadedFile findByIdAndOwner(String id, String owner);
	UserUploadedFile findByIdAndOwnerAndFacilityId(String id, String owner, String facility);
}
