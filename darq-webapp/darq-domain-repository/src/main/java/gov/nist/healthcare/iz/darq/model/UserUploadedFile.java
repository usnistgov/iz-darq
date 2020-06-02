package gov.nist.healthcare.iz.darq.model;

import gov.nist.healthcare.iz.darq.digest.domain.ADFMetaData;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Summary;

import java.util.Date;
import java.util.Set;

public class UserUploadedFile extends ADFMetaData {

    String facilityId;

    public UserUploadedFile(String name, String path, String owner, Date analysedOn, Date uploadedOn, ConfigurationPayload configuration, String keyHash, Summary summary, String size, String version, String build, String mqeVersion, Set<String> inactiveDetections, String facilityId) {
        super(name, path, owner, analysedOn, uploadedOn, configuration, keyHash, summary, size, version, build, mqeVersion, inactiveDetections);
        this.facilityId = facilityId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }
}
