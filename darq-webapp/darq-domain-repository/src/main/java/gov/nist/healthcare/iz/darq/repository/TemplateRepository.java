package gov.nist.healthcare.iz.darq.repository;

import java.util.List;

import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

//import gov.nist.healthcare.iz.darq.analyzer.domain.ReportTemplate;

public interface TemplateRepository extends MongoRepository<ReportTemplate, String>{

	List<ReportTemplate> findByOwner(String owner);
	ReportTemplate findByIdAndOwner(String id, String owner);
	@Query("{ '$or' : [ {'owner' : { $eq: ?0 }}, {'published' : {'$eq' : true }}]}")
	List<ReportTemplate> findAccessible(String owner);
	@Query("{ '$and' : [ { _id : ?0 } , {'$or' : [ {'owner' : { $eq: ?1 }}, {'published' : {'$eq' : true }}] } ] }")
	ReportTemplate findMineOrReadOnly(String id, String owner);
}
