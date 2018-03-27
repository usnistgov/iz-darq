package gov.nist.healthcare.iz.darq.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import gov.nist.healthcare.iz.darq.digest.domain.ADFMetaData;

public interface ADFMetaDataRepository extends MongoRepository<ADFMetaData, String>{

	List<ADFMetaData> findByOwner(String owner);
	ADFMetaData findByIdAndOwner(String id, String owner);
}
