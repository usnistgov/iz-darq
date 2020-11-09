package gov.nist.healthcare.iz.darq.repository;

import gov.nist.healthcare.iz.darq.model.EmailTemplate;
import gov.nist.healthcare.iz.darq.model.EmailType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailTemplateRepository extends MongoRepository<EmailTemplate, EmailType> {

}
