package gov.nist.healthcare.iz.darq.repository;

import gov.nist.healthcare.iz.darq.model.ADFMergeJob;
import gov.nist.healthcare.iz.darq.model.JobStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ADFMergeJobRepository extends MongoRepository<ADFMergeJob, String> {
    List<ADFMergeJob> findByStatusIn(List<JobStatus> status);
    boolean existsByIdAndOwnerId(String id, String ownerId);
    List<ADFMergeJob> findByOwnerId(String ownerId);
    List<ADFMergeJob> findByOwnerIdAndFacilityId(String ownerId, String facilityId);
    List<ADFMergeJob> findByOwnerIdAndFacilityIdIsNull(String ownerId);
    ADFMergeJob findByIdAndOwnerId(String id, String ownerId);
}
