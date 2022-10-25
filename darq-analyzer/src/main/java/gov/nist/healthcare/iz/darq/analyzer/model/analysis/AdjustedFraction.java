package gov.nist.healthcare.iz.darq.analyzer.model.analysis;

import gov.nist.healthcare.iz.darq.analyzer.model.variable.QueryVariableRefInstance;
import gov.nist.healthcare.iz.darq.digest.domain.Fraction;

public class AdjustedFraction extends Fraction {
    QueryVariableRefInstance denominatorVariable;
    QueryVariableRefInstance numeratorVariable;

    public QueryVariableRefInstance getNumeratorVariable() {
        return numeratorVariable;
    }

    public void setNumeratorVariable(QueryVariableRefInstance numeratorVariable) {
        this.numeratorVariable = numeratorVariable;
    }

    public QueryVariableRefInstance getDenominatorVariable() {
        return denominatorVariable;
    }

    public void setDenominatorVariable(QueryVariableRefInstance denominatorVariable) {
        this.denominatorVariable = denominatorVariable;
    }
}
