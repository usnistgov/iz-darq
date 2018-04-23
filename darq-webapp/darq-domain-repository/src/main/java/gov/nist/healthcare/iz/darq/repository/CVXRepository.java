package gov.nist.healthcare.iz.darq.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import gov.nist.healthcare.iz.darq.model.CVXCode;

public interface CVXRepository extends MongoRepository<CVXCode, String>{

}
