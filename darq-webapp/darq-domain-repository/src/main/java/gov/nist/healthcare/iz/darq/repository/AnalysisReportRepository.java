package gov.nist.healthcare.iz.darq.repository;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalysisReportRepository extends MongoRepository<AnalysisReport, String> {

    AnalysisReport findByIdAndOwnerId(String id, String ownerId);
    AnalysisReport findByIdAndOwnerIdAndPublished(String id, String ownerId, boolean published);
    AnalysisReport findByIdAndPublished(String id, boolean published);
    List<AnalysisReport> findByPublishedAndOwnerIdAndFacilityId(boolean published, String ownerId, String facility);
    List<AnalysisReport> findByPublishedAndFacilityId(boolean published, String facilityId);
    List<AnalysisReport> findByOwnerIdAndFacilityIdIsNull(String id);
}
