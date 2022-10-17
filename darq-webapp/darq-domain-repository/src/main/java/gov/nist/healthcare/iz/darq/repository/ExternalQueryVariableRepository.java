package gov.nist.healthcare.iz.darq.repository;

import gov.nist.healthcare.iz.darq.model.ExternalQueryVariable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExternalQueryVariableRepository extends MongoRepository<ExternalQueryVariable, String> {
}
