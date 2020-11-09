package gov.nist.healthcare.iz.darq.repository;

import java.util.List;

import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface TemplateRepository extends MongoRepository<ReportTemplate, String>{

	List<ReportTemplate> findByOwnerId(String ownerId);
	ReportTemplate findByIdAndOwnerId(String id, String ownerId);
	@Query("{ '$or' : [ {'ownerId' : { $eq: ?0 }}, {'published' : {'$eq' : true }}]}")
	List<ReportTemplate> findAccessibleTo(String ownerId);
	@Query("{ '$and' : [ { _id : ?0 } , {'$or' : [ {'ownerId' : { $eq: ?1 }}, {'published' : {'$eq' : true }}] } ] }")
	ReportTemplate findByOwnerIdOrReadOnly(String id, String ownerId);
}
