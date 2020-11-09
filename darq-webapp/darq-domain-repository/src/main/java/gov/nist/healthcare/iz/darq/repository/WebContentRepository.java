package gov.nist.healthcare.iz.darq.repository;

import gov.nist.healthcare.iz.darq.model.WebContent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebContentRepository extends MongoRepository<WebContent, String> {
}
