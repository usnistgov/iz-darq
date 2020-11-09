package gov.nist.healthcare.iz.darq.repository;

import gov.nist.healthcare.iz.darq.model.ToolConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolConfigurationRepository extends MongoRepository<ToolConfiguration, String> {
}
