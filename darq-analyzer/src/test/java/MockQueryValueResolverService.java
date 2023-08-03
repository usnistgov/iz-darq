import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.analyzer.model.variable.QueryVariableRef;
import gov.nist.healthcare.iz.darq.analyzer.model.variable.QueryVariableRefInstance;
import gov.nist.healthcare.iz.darq.analyzer.service.common.QueryValueResolverService;

public class MockQueryValueResolverService implements QueryValueResolverService {
	@Override
	public QueryVariableRefInstance resolveInstanceValue(QueryVariableRef value, ADFReader file, String facilityId) throws Exception {
		return null;
	}
}
