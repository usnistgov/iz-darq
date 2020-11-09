package gov.nist.healthcare.iz.darq.repository;

import java.util.List;

import gov.nist.healthcare.iz.darq.model.UserUploadedFile;
import org.springframework.data.mongodb.repository.MongoRepository;

import gov.nist.healthcare.iz.darq.digest.domain.ADFMetaData;

public interface ADFMetaDataRepository extends MongoRepository<UserUploadedFile, String>{
	List<UserUploadedFile> findByOwnerId(String ownerId);
	List<UserUploadedFile> findByOwnerIdAndFacilityId(String ownerId, String facility);
	List<UserUploadedFile> findByOwnerIdAndFacilityIdIsNull(String ownerId);
	List<UserUploadedFile> findByFacilityId(String facility);
	UserUploadedFile findByIdAndOwnerId(String id, String ownerId);
	UserUploadedFile findByIdAndOwnerIdAndFacilityId(String id, String ownerId, String facility);
}
