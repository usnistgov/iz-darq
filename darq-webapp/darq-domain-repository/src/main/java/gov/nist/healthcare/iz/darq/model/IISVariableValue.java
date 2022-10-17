package gov.nist.healthcare.iz.darq.model;

import java.util.Date;
import java.util.Objects;

public class IISVariableValue {
    private double value;
    private String comment;
    private Date dateValueUpdated;
    private Date dateUpdated;
    private String facilityId;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDateValueUpdated() {
        return dateValueUpdated;
    }

    public void setDateValueUpdated(Date dateValueUpdated) {
        this.dateValueUpdated = dateValueUpdated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IISVariableValue that = (IISVariableValue) o;
        return facilityId.equals(that.facilityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facilityId);
    }
}
