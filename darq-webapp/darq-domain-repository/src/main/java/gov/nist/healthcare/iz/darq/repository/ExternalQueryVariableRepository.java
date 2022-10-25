package gov.nist.healthcare.iz.darq.repository;

import gov.nist.healthcare.iz.darq.analyzer.model.variable.ExternalQueryVariableScope;
import gov.nist.healthcare.iz.darq.model.ExternalQueryVariable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ExternalQueryVariableRepository extends MongoRepository<ExternalQueryVariable, String> {
    List<ExternalQueryVariable> findByScope(ExternalQueryVariableScope scope);
}
