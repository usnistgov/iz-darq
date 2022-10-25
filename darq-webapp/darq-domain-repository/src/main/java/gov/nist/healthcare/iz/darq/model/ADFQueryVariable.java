package gov.nist.healthcare.iz.darq.model;

import gov.nist.healthcare.iz.darq.analyzer.model.variable.QueryVariableType;

public class ADFQueryVariable extends QueryVariable {
    public ADFQueryVariable() {
        super(QueryVariableType.ADF);
    }
    public ADFQueryVariable(ADFQueryVariableID id, String name, String description) {
        super(id.toString(), name, description, QueryVariableType.ADF);
    }
}
