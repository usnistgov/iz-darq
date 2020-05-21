package gov.nist.healthcare.iz.darq.model;

import gov.nist.healthcare.iz.darq.digest.domain.ADFMetaData;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Summary;

import java.util.Date;

public class UserUploadedFile extends ADFMetaData {

    String facilityId;

    public UserUploadedFile(String name, String path, String owner, Date analysedOn, Date uploadedOn, ConfigurationPayload configuration, String keyHash, Summary summary, String size, String facilityId) {
        super(name, path, owner, analysedOn, uploadedOn, configuration, keyHash, summary, size);
        this.facilityId = facilityId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }
}
