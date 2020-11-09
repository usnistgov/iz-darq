package gov.nist.healthcare.iz.darq.repository;

import gov.nist.healthcare.iz.darq.model.AnalysisJob;
import gov.nist.healthcare.iz.darq.model.JobStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalysisJobRepository extends MongoRepository<AnalysisJob, String> {

    List<AnalysisJob> findByStatusIn(List<JobStatus> status);
    boolean existsByIdAndOwnerId(String id, String ownerId);
    List<AnalysisJob> findByOwnerId(String ownerId);
    List<AnalysisJob> findByReportId(String reportId);
    List<AnalysisJob> findByOwnerIdAndFacilityId(String ownerId, String facilityId);
    List<AnalysisJob> findByOwnerIdAndFacilityIdIsNull(String ownerId);
    AnalysisJob findByIdAndOwnerId(String id, String ownerId);

}
