package gov.nist.healthcare.iz.darq.analyzer.service;

import gov.nist.healthcare.iz.darq.analyzer.model.variable.QueryVariableRef;
import gov.nist.healthcare.iz.darq.analyzer.model.variable.QueryVariableRefInstance;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;

public interface QueryValueResolverService {

    QueryVariableRefInstance resolveInstanceValue(QueryVariableRef value, ADFile file, String facilityId) throws Exception;

}
