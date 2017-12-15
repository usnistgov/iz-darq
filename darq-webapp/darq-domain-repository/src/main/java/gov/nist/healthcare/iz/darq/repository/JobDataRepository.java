package gov.nist.healthcare.iz.darq.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gov.nist.healthcare.iz.darq.batch.domain.JobData;

@Repository
public interface JobDataRepository extends MongoRepository<JobData, String>{

	public List<JobData> findByUser(String user);
}
