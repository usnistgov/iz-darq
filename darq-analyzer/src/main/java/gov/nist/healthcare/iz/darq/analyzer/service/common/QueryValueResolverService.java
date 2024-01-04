package gov.nist.healthcare.iz.darq.analyzer.service.common;

import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.analyzer.model.variable.QueryVariableRef;
import gov.nist.healthcare.iz.darq.analyzer.model.variable.QueryVariableRefInstance;

public interface QueryValueResolverService {

    QueryVariableRefInstance resolveInstanceValue(QueryVariableRef value, ADFReader file, String facilityId) throws Exception;

}
