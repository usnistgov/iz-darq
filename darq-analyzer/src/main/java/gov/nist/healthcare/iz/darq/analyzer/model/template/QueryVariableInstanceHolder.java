package gov.nist.healthcare.iz.darq.analyzer.model.template;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AdjustedFraction;
import gov.nist.healthcare.iz.darq.analyzer.model.variable.QueryVariableRefInstance;
import gov.nist.healthcare.iz.darq.digest.domain.Fraction;

public class QueryVariableInstanceHolder {
    private QueryVariableRefInstance denominator;
    private QueryVariableRefInstance numerator;

    public boolean hasDenominator() {
        return this.denominator != null;
    }

    public boolean hasNumerator() {
        return this.numerator != null;
    }

    public AdjustedFraction getAdjustedFraction(Fraction fraction) {
        if(this.hasDenominator() || this.hasNumerator()) {
            AdjustedFraction adjustedFraction = new AdjustedFraction();
            adjustedFraction.setDenominatorVariable(this.denominator);
            adjustedFraction.setNumeratorVariable(this.numerator);
            adjustedFraction.setCount(this.hasNumerator() ? (int) this.numerator.getValue() : fraction.getCount());
            adjustedFraction.setTotal(this.hasDenominator() ? (int) this.denominator.getValue() : fraction.getTotal());
            return adjustedFraction;
        } else {
            return null;
        }
    }

    public QueryVariableRefInstance getDenominator() {
        return denominator;
    }

    public void setDenominator(QueryVariableRefInstance denominator) {
        this.denominator = denominator;
    }

    public QueryVariableRefInstance getNumerator() {
        return numerator;
    }

    public void setNumerator(QueryVariableRefInstance numerator) {
        this.numerator = numerator;
    }
}
