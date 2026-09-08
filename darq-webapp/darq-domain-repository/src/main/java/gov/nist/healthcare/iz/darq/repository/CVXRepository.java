package gov.nist.healthcare.iz.darq.repository;

import gov.nist.healthcare.iz.darq.model.CVXCode;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CVXRepository extends MongoRepository<CVXCode, String>{

}
