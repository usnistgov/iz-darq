package gov.nist.healthcare.iz.darq.repository;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalysisReportRepository extends MongoRepository<AnalysisReport, String> {

    AnalysisReport findByIdAndOwner(String id, String owner);
    AnalysisReport findByIdAndOwnerAndPublished(String id, String owner, boolean published);
    List<AnalysisReport> findByPublishedAndOwnerAndFacilityId(boolean published, String owner, String facility);
    List<AnalysisReport> findByPublishedAndFacilityId(boolean published, String facilityId);

}
