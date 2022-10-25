package gov.nist.healthcare.iz.darq.model;

import gov.nist.healthcare.iz.darq.analyzer.model.variable.ExternalQueryVariableScope;

import java.util.Date;

public class GlobalExternalQueryVariable extends ExternalQueryVariable {
    private int value;
    private Date dateValueUpdated;
    private String comment;

    public GlobalExternalQueryVariable() {
        super(ExternalQueryVariableScope.GLOBAL);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Date getDateValueUpdated() {
        return dateValueUpdated;
    }

    public void setDateValueUpdated(Date dateValueUpdated) {
        this.dateValueUpdated = dateValueUpdated;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
