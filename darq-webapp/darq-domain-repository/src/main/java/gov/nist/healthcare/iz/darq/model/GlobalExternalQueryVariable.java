package gov.nist.healthcare.iz.darq.model;

import java.util.Date;

public class GlobalExternalQueryVariable extends ExternalQueryVariable {
    private double value;
    private Date dateValueUpdated;
    private String comment;

    public GlobalExternalQueryVariable() {
        super(ExternalQueryVariableScope.GLOBAL);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
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
