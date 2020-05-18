package gov.nist.healthcare.iz.darq.repository;

import gov.nist.healthcare.iz.darq.model.AnalysisJob;
import gov.nist.healthcare.iz.darq.model.JobStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalysisJobRepository extends MongoRepository<AnalysisJob, String> {

    List<AnalysisJob> findByStatus(JobStatus status);
}
