package gov.nist.healthcare.iz.darq.analyzer.model.variable;

import java.util.Date;

public class QueryVariableRefInstance extends QueryVariableRef {
    private double value;
    private Date timestamp;
    private String facilityId;
    private String comment;
    private String name;
    private String description;

    public QueryVariableRefInstance(String id, QueryVariableType type, ExternalQueryVariableScope scope, double value, Date timestamp, String facilityId, String comment, String name, String description) {
        super(id, type, scope, QueryValueType.STATIC);
        this.value = value;
        this.timestamp = timestamp;
        this.facilityId = facilityId;
        this.comment = comment;
        this.name = name;
        this.description = description;
    }


    public QueryVariableRefInstance() {
        super(QueryValueType.STATIC);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

