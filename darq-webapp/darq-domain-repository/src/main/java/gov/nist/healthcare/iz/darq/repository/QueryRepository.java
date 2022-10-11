package gov.nist.healthcare.iz.darq.repository;

import gov.nist.healthcare.iz.darq.model.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface QueryRepository extends MongoRepository<Query, String> {
    List<Query> findByOwnerId(String ownerId);
}
