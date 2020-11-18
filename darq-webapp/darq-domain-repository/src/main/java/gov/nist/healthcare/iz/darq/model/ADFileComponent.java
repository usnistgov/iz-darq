package gov.nist.healthcare.iz.darq.model;
import gov.nist.healthcare.iz.darq.digest.domain.Summary;

import java.util.Date;

public class ADFileComponent {
    String id;
    String name;
    String ownerId;
    Date analysedOn;
    Date uploadedOn;
    String size;
    Summary summary;
    String facilityId;

    public ADFileComponent(String id, String name, String ownerId, Date analysedOn, Date uploadedOn, String size, Summary summary, String facilityId) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.analysedOn = analysedOn;
        this.uploadedOn = uploadedOn;
        this.size = size;
        this.summary = summary;
        this.facilityId = facilityId;
    }

    public ADFileComponent() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Date getAnalysedOn() {
        return analysedOn;
    }

    public void setAnalysedOn(Date analysedOn) {
        this.analysedOn = analysedOn;
    }

    public Date getUploadedOn() {
        return uploadedOn;
    }

    public void setUploadedOn(Date uploadedOn) {
        this.uploadedOn = uploadedOn;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }
}
