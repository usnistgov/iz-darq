package gov.nist.healthcare.iz.darq.repository;

import gov.nist.healthcare.iz.darq.model.AnalysisJob;
import gov.nist.healthcare.iz.darq.model.JobStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalysisJobRepository extends MongoRepository<AnalysisJob, String> {

    List<AnalysisJob> findByStatusIn(List<JobStatus> status);
    boolean existsByIdAndOwner(String id, String owner);
    List<AnalysisJob> findByOwner(String owner);
    List<AnalysisJob> findByReportId(String reportId);
    List<AnalysisJob> findByOwnerAndFacilityId(String owner, String facilityId);
    AnalysisJob findByIdAndOwner(String id, String owner);

}
