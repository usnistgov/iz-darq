package gov.nist.healthcare.iz.darq.repository;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisReportRepository extends MongoRepository<AnalysisReport, String> {
}
