package gov.nist.healthcare.iz.darq.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gov.nist.healthcare.iz.darq.analysis.AnalysisRawResult;


@Repository
public interface AnalysisResultRepository extends MongoRepository<AnalysisRawResult, String>{

}
