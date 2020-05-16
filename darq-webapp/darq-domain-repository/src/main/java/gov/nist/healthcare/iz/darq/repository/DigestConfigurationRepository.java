package gov.nist.healthcare.iz.darq.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import gov.nist.healthcare.iz.darq.model.DigestConfiguration;

public interface DigestConfigurationRepository extends MongoRepository<DigestConfiguration, String>{

	List<DigestConfiguration> findByOwner(String owner);
	DigestConfiguration findByOwnerAndId(String owner, String id);
	@Query("{ '$or' : [ {'owner' : { $eq: ?0 }}, {'published' : {'$eq' : true }}]}")
	List<DigestConfiguration> findAccessible(String owner);
	@Query("{ '$and' : [ { _id : ?0 } , {'$or' : [ {'owner' : { $eq: ?1 }}, {'published' : {'$eq' : true }}] } ] }")
	DigestConfiguration findMineOrReadOnly(String id, String owner);
	
}
