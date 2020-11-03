package gov.nist.healthcare.iz.darq.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import gov.nist.healthcare.iz.darq.model.DigestConfiguration;

public interface DigestConfigurationRepository extends MongoRepository<DigestConfiguration, String>{

	List<DigestConfiguration> findByOwnerId(String ownerId);
	DigestConfiguration findByOwnerIdAndId(String ownerId, String id);
	@Query("{ '$or' : [ {'ownerId' : { $eq: ?0 }}, {'published' : {'$eq' : true }}]}")
	List<DigestConfiguration> findAccessibleTo(String ownerId);
	@Query("{ '$and' : [ { _id : ?0 } , {'$or' : [ {'ownerId' : { $eq: ?1 }}, {'published' : {'$eq' : true }}] } ] }")
	DigestConfiguration findByOwnerIdOrReadOnly(String id, String ownerId);
	
}
