package gov.nist.healthcare.iz.darq.model;

import java.util.HashSet;
import java.util.Set;

public class IISExternalQueryVariable extends ExternalQueryVariable {
    private Set<IISVariableValue> values;

    public IISExternalQueryVariable() {
        super(ExternalQueryVariableScope.IIS);
    }

    public Set<IISVariableValue> getValues() {
        if(values == null) {
            this.values = new HashSet<>();
        }
        return values;
    }

    public void setValues(Set<IISVariableValue> values) {
        this.values = values;
    }
}
